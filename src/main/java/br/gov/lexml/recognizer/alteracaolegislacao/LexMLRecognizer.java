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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import br.gov.lexml.parser.documentoarticulado.LexMLParser;

public class LexMLRecognizer {

	private static HashMap<String, String[]> dispositivos_modificadores = new HashMap<String, String[]>();

	private final String IGNORE_CASE_REGEX = "(?i)";

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
			String content = dispositivo.getTextContent();
			List<AlteracaoDispositivo> listaAlteracao = recognizeChanges(content);
			for (AlteracaoDispositivo alteracao : listaAlteracao)
				lista.add(alteracao.toString());
		}
		return lista;
	}

	private List<AlteracaoDispositivo> recognizeChanges(String content) {
		List<AlteracaoDispositivo> lista = new ArrayList<AlteracaoDispositivo>();
		for (Object key : dispositivos_modificadores.keySet()) {
			String[] regex = dispositivos_modificadores.get(key);
			for (String rule : regex)
				if (Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(content).find())
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
				if (Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(line).find())
					return key.toString();
		}
		return "";
	}

	private String extractMatch(String line, String[] regex) {
		for (String rule : regex) {
			Matcher matcher = Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(line);
			if (matcher.find())
				return matcher.group(1);
		}
		return null;
	}

	private List<String> extractArtRevogacao(String line, String[] regex) {
		for (String rule : regex) {
			Matcher matcher = Pattern.compile(IGNORE_CASE_REGEX + rule + "(.*art\\.\\s+\\d+)+").matcher(line);
			List<String> ocorrencias = new ArrayList<String>();
			if (matcher.find()) {
				Matcher matcher1 = Pattern.compile("art\\.\\s+\\d+", Pattern.CASE_INSENSITIVE).matcher(matcher.group(1));
				while (matcher1.find())
					ocorrencias.add(matcher1.group().replace(".", "").replace(" ", ""));
				return ocorrencias;
			}
			matcher = Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(line);
			if (matcher.find()) {
				Matcher matcher_par = Pattern.compile("§.*\\s([0-9])º do (artigo\\s+\\d+)+", Pattern.CASE_INSENSITIVE).matcher(line);
				while (matcher_par.find())
					ocorrencias.add(matcher_par.group(2).replace("artigo", "art").replace(" ", "") + "_par" + matcher_par.group(1));
				Matcher matcher2 = Pattern.compile("(artigo\\s+\\d+)+", Pattern.CASE_INSENSITIVE).matcher(line);
				while (matcher2.find())
					ocorrencias.add(matcher2.group().replace("artigo", "art").replace(" ", ""));
				return ocorrencias;
			}
		}
		return null;
	}

	private List<String> extractAcrescimo(String trecho, String[] regex) {
		List<String> ocorrencias = new ArrayList<String>();
		for (String rule : regex) {
			Matcher matcher2 = Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(trecho);
			if (matcher2.find()) {
				Matcher mtc = Pattern.compile("(art\\.\\s+\\d+(-\\p{L})?)", Pattern.CASE_INSENSITIVE).matcher(trecho.substring(matcher2.start()));
				while (mtc.find())
					ocorrencias.add(mtc.group().replace(".", "").replace(" ", "").toLowerCase().replace("-a", "-A").replace("-b", "-B"));
			}
		}
		return ocorrencias;
	}

	private List<String> extractArtNovaRedacao(String line, String[] regex) {
		String prepare = null;
		List<String> ocorrencias = new ArrayList<String>();
		for (String rule : regex) {
			Matcher matcher1 = Pattern.compile(".*\\s*.(inciso.\\s*.[A-Z]+).*\\s*(art\\.*.[0-9]+).*\\s*" + rule, Pattern.CASE_INSENSITIVE).matcher(line);
			if (matcher1.find()) {
				prepare = matcher1.group(2).replace(".", "").replace(" ", "");
				prepare += "_inc" + traduzirNumeralRomano(matcher1.group(1).replace("inciso", ""));
				ocorrencias.add(prepare);
			}
			Matcher matcher2 = Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(line);
			if (matcher2.find()) {
				Matcher mtc = Pattern.compile("(art\\.\\s+\\d+(-\\p{L})?)", Pattern.CASE_INSENSITIVE).matcher(line.substring(matcher2.start()));
				while (mtc.find())
					ocorrencias.add(mtc.group().replace(".", "").replace(" ", "").toLowerCase());
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
}
