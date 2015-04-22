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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.gov.lexml.parser.documentoarticulado.LexMLParser;

public class LexMLRecognizer {

	private static HashMap<String, String[]> dispositivos_modificadores = new HashMap<String, String[]>();

	private LexMLParser lexMLParser;

	public LexMLRecognizer(LexMLParser lexMLParser) {
		this.lexMLParser = lexMLParser;
		dispositivos_modificadores.put("revogacao", new String[] { "Fica revogado o", "Revoga-se o", "revogadas", "Revoga o" });
		dispositivos_modificadores.put("novaredacao", new String[] { "(passa|passam) a vigorar com (a|as) (seguinte|seguintes)" });
		dispositivos_modificadores.put("acrescimo", new String[] { "passa a vigorar acrescido" });
	}

	public List<String> getDispositivosModificadores() {
		List<String> lista = new ArrayList<String>();
		for (Element blocoAlteracao : blocosAlteracao())
			for (AlteracaoDispositivo alteracao : recognizeChanges(blocoAlteracao))
				lista.add(alteracao.toString());
		for (Element dispositivo : lexMLParser.getArtigos()) {
			String trecho = dispositivo.getTextContent();
			for (String rule : dispositivos_modificadores.get("revogacao"))
				if (matcherCompile(rule, trecho).find()) {
					for (AlteracaoDispositivo alteracao : extractArtRevogacao(trecho, "revogacao", rule))
						lista.add(alteracao.toString());
					break;
				}
		}
		return lista;
	}

	private List<Element> blocosAlteracao() {
		List<Element> lista = new ArrayList<Element>();
		for (Element dispositivo : lexMLParser.getArtigos())
			adicionarBlocosAlteracao(dispositivo, lista);
		return lista;
	}

	private void adicionarBlocosAlteracao(Element dispositivo, List<Element> lista) {
		if (dispositivo.getNodeName().equals("Alteracao")) {
			lista.add(dispositivo);
			return;
		}
		NodeList list = dispositivo.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
				adicionarBlocosAlteracao((Element) list.item(i), lista);
	}

	private List<AlteracaoDispositivo> recognizeChanges(Element blocoAlteracao) {
		List<AlteracaoDispositivo> lista = new ArrayList<AlteracaoDispositivo>();
		String trecho = blocoAlteracao.getPreviousSibling().getPreviousSibling().getTextContent();
		for (String rule : dispositivos_modificadores.get("novaredacao"))
			if (matcherCompile(rule, trecho).find()) {
				extractDispositivoChanged(blocoAlteracao, "novaredacao", "", lista);
				return lista;
			}
		extractDispositivoChanged(blocoAlteracao, "acrescimo", "", lista);
		return lista;
	}

	private void extractDispositivoChanged(Element blocoAlteracao, String key, String href, List<AlteracaoDispositivo> lista) {
		NodeList list = blocoAlteracao.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) list.item(i);
				if (element.getNodeName().equals("p"))
					lista.add(alteracao(key, href));
				else if (element.hasAttribute("xlink:href"))
					extractDispositivoChanged(element, key, element.getAttribute("xlink:href"), lista);
				else
					extractDispositivoChanged(element, key, href, lista);
			}
	}

	/**
	 * Para resgatar a data da vigência deve-se verificar se existe
	 * prioritariamente: 1º Data Vigor No artigo modificador 2º Data de
	 * Publicação do documento 3º Data da Assinatura
	 *
	 * @return String datavigencia
	 */
	private String getDataVigencia() {
		if (lexMLParser.getDataVigor() != null)
			return lexMLParser.getDataVigor();
		return lexMLParser.getDataPublicacao() == null ? lexMLParser.getDataAssinatura() : lexMLParser.getDataPublicacao();
	}

	private List<AlteracaoDispositivo> extractArtRevogacao(String line, String key, String rule) {
		List<AlteracaoDispositivo> ocorrencias = new ArrayList<AlteracaoDispositivo>();
		Matcher matcher = matcherCompile(rule + "(.*art\\.\\s+\\d+)+", line);
		if (matcher.find()) {
			Matcher paraNum = matcherCompile("§\\s+(\\d+)\\p{L}.do.(art\\.\\s+\\d+)", matcher.group(1));
			while (paraNum.find())
				ocorrencias.add(alteracao(key, paraNum.group(2) + "_par" + paraNum.group(1)));
			Matcher paraTex = matcherCompile("Par\\p{L}grafo\\s+(\\p{L}+).do.(art\\.\\s+\\d+)", matcher.group(1));
			while (paraTex.find())
				ocorrencias.add(alteracao(key, paraTex.group(2) + "_par" + paraTex.group(1).replaceAll("\\p{L}nico", "1")));
			Matcher matcher1 = matcherCompile("art\\.\\s+\\d+", matcher.group(1));
			while (matcher1.find())
				ocorrencias.add(alteracao(key, matcher1.group()));
			return ocorrencias;
		}
		matcher = matcherCompile(rule, line);
		if (matcher.find()) {
			Matcher matcher_par = matcherCompile("§.*\\s([0-9])º do (artigo\\s+\\d+)+", line);
			while (matcher_par.find())
				ocorrencias.add(alteracao(key, matcher_par.group(2) + "_par" + matcher_par.group(1)));
			Matcher matcher2 = matcherCompile("(artigo\\s+\\d+)+", line);
			while (matcher2.find())
				ocorrencias.add(alteracao(key, matcher2.group()));
			return ocorrencias;
		}
		return null;
	}

	private AlteracaoDispositivo alteracao(String key, String id) {
		return new AlteracaoDispositivo(key, formatArtOutput(id), getDataVigencia());
	}

	private Matcher matcherCompile(String key, String content) {
		return Pattern.compile(key, Pattern.CASE_INSENSITIVE).matcher(content);
	}

	private String formatArtOutput(String out) {
		return out.replace(".", "").replace(" ", "").replace("artigo", "art").toLowerCase().replace("-a", "-A").replace("-b", "-B");
	}
}
