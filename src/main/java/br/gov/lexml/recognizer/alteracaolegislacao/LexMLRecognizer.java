/**
    lexml-recognizer-alteracao-legislacao
    Copyright (C) 2014-2015  LexML Brasil

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.gov.lexml.recognizer.alteracaolegislacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import br.gov.lexml.parser.documentoarticulado.LexMLParser;

public class LexMLRecognizer {

	private static HashMap<String, String[]> dispositivos_modificadores = new HashMap<String, String[]>();

	private LexMLParser lexMLParser;

	public LexMLRecognizer(LexMLParser lexMLParser) {
		this.lexMLParser = lexMLParser;
		initRegex();
	}

	private void initRegex() {
		dispositivos_modificadores.put("revogacao", new String[] { "Fica revogado o", "Revoga-se o", "revogadas", "Revoga o" });
		dispositivos_modificadores.put("novaredacao", new String[] { "(passa|passam) a vigorar com (a|as) (seguinte|seguintes)" });
		dispositivos_modificadores.put("acrescimo", new String[] { "passa a vigorar acrescido" });
	}

	public List<String> getDispositivosModificadores() {
		List<String> lista = new ArrayList<String>();
		for (Element dispositivo : lexMLParser.getArtigos()) {
			List<AlteracaoDispositivo> listaAlteracao = recognizeChanges(dispositivo);
			for (AlteracaoDispositivo alteracao : listaAlteracao)
				lista.add(alteracao.toString());
		}
		return lista;
	}

	private List<AlteracaoDispositivo> recognizeChanges(Element dispositivo) {
		String content = dispositivo.getTextContent();
		List<AlteracaoDispositivo> lista = new ArrayList<AlteracaoDispositivo>();
		for (Object key : dispositivos_modificadores.keySet()) {
			String[] regex = dispositivos_modificadores.get(key);
			for (String rule : regex)
				if (matcherCompile(rule, content).find())
					for (String dispositivoChanged : getDispositivoChanged(content))
						lista.add(new AlteracaoDispositivo(getTypeChange(content), dispositivoChanged, getDataVigencia(content)));
		}
		return lista;
	}

	/**
	 * Para resgatar a data da vigência deve-se verificar se existe prioritariamente: 1º Data Vigor No artigo modificador 2º Data de Publicação do documento 3º Data da Assinatura
	 *
	 * @param String
	 *            trecho
	 * @return String datavigencia
	 */
	private String getDataVigencia(String trecho) {
		if (lexMLParser.getDataVigor() != null)
			return lexMLParser.getDataVigor();
		return lexMLParser.getDataPublicacao() == null ? lexMLParser.getDataAssinatura() : lexMLParser.getDataPublicacao();
	}

	private List<String> getDispositivoChanged(String trecho) {
		String[] regex;
		List<String> listDispositivos = new ArrayList<String>();
		switch (getTypeChange(trecho)) {
		case "revogacao":
			regex = dispositivos_modificadores.get(getTypeChange(trecho));
			listDispositivos = extractArtRevogacao(trecho, regex);
			break;
		case "novaredacao":
			regex = dispositivos_modificadores.get(getTypeChange(trecho));
			listDispositivos = extractArtNovaRedacao(trecho, regex);
			break;
		case "acrescimo":
			regex = dispositivos_modificadores.get(getTypeChange(trecho));
			listDispositivos = extractAcrescimo(trecho, regex);
			break;
		}
		return listDispositivos;
	}

	private String getTypeChange(String line) {
		for (Object key : dispositivos_modificadores.keySet()) {
			String[] regex = dispositivos_modificadores.get(key);
			for (String rule : regex)
				if (matcherCompile(rule, line).find())
					return key.toString();
		}
		return "";
	}

	private List<String> extractArtRevogacao(String line, String[] regex) {
		List<String> ocorrencias = new ArrayList<String>();
		Matcher matcher;
		for (String rule : regex) {
			matcher = matcherCompile(rule + "(.*art\\.\\s+\\d+)+", line);
			if (matcher.find()) {
				Matcher paraNum = matcherCompile("§\\s+(\\d+)\\p{L}.do.(art\\.\\s+\\d+)", matcher.group(1));
				while (paraNum.find())
					ocorrencias.add(formatArtOutput(paraNum.group(2)) + "_par" + paraNum.group(1));

				Matcher paraTex = matcherCompile("Par\\p{L}grafo\\s+(\\p{L}+).do.(art\\.\\s+\\d+)", matcher.group(1));
				while (paraTex.find())
					ocorrencias.add(formatArtOutput(paraTex.group(2)) + "_par" + paraTex.group(1).replaceAll("\\p{L}nico", "1"));

				Matcher matcher1 = matcherCompile("art\\.\\s+\\d+", matcher.group(1));
				while (matcher1.find())
					ocorrencias.add(formatArtOutput(matcher1.group()));
				return ocorrencias;
			}

			matcher = matcherCompile(rule, line);
			if (matcher.find()) {
				Matcher matcher_par = matcherCompile("§.*\\s([0-9])º do (artigo\\s+\\d+)+", line);
				while (matcher_par.find())
					ocorrencias.add(formatArtOutput(matcher_par.group(2)) + "_par" + matcher_par.group(1));
				Matcher matcher2 = matcherCompile("(artigo\\s+\\d+)+", line);
				while (matcher2.find())
					ocorrencias.add(formatArtOutput(matcher2.group()));
				return ocorrencias;
			}
		}
		return null;
	}

	private List<String> extractAcrescimo(String trecho, String[] regex) {
		List<String> ocorrencias = new ArrayList<String>();
		for (String rule : regex) {
			Matcher matcher2 = matcherCompile(rule, trecho);
			if (matcher2.find()) {
				Matcher mtc = matcherCompile("(art\\.\\s+\\d+(-\\p{L})?)", trecho.substring(matcher2.start()));
				while (mtc.find())
					ocorrencias.add(formatArtOutput(mtc.group()));
			}
		}
		return ocorrencias;
	}

	private List<String> extractArtNovaRedacao(String line, String[] regex) {
		String prepare = null;
		List<String> ocorrencias = new ArrayList<String>();
		for (String rule : regex) {
			Matcher matcher1 = matcherCompile(".*\\s*.(inciso.\\s*.[A-Z]+).*\\s*(art\\.*.[0-9]+).*\\s*" + rule, line);
			if (matcher1.find()) {
				prepare = formatArtOutput(matcher1.group(2));
				prepare += "_inc" + traduzirNumeralRomano(matcher1.group(1).replace("inciso", ""));
				ocorrencias.add(prepare);
			}
			Matcher matcher2 = matcherCompile(rule, line);
			if (matcher2.find()) {
				Matcher mtc = matcherCompile("(art\\.\\s+\\d+(-\\p{L})?)", line.substring(matcher2.start()));
				while (mtc.find())
					ocorrencias.add(formatArtOutput(mtc.group()));
			}
		}
		return ocorrencias;
	}

	private int traduzirNumeralRomano(String texto) {
		int n = 0;
		int numeralDaDireita = 0;
		for (int i = texto.length() - 1; i >= 0; i--) {
			int valor = (int) traduzirNumeralRomano(texto.charAt(i));
			n += valor * Math.signum(valor + 0.5 - numeralDaDireita);
			numeralDaDireita = valor;
		}
		return n;
	}

	private double traduzirNumeralRomano(char caractere) {
		return Math.floor(Math.pow(10, "IXCM".indexOf(caractere))) + 5 * Math.floor(Math.pow(10, "VLD".indexOf(caractere)));
	}

	private Matcher matcherCompile(String key, String content) {
		return Pattern.compile(key, Pattern.CASE_INSENSITIVE).matcher(content);
	}

	private String formatArtOutput(String out) {
		return out.replace(".", "").replace(" ", "").replace("artigo", "art").toLowerCase().replace("-a", "-A").replace("-b", "-B");
	}
}
