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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
		dispositivos_modificadores.put("revogacao", new String[] { "Fica revogado o", "Revoga-se o" });
		dispositivos_modificadores.put("novaredacao", new String[] { "passa a vigorar com a seguinte" });
	}

	public List<String> getDispositivosModificadores() {
		List<String> lista = new ArrayList<String>();
		for (Element dispositivo : lexMLParser.getArtigos()) {
			String content = dispositivo.getElementsByTagName("p").item(0).getTextContent();
			List<AlteracaoDispositivo> listaAlteracao = recognizeChanges(content);
			for (AlteracaoDispositivo alteracao : listaAlteracao) {
				lista.add(alteracao.toString());
			}
		}
		return lista;
	}

	private List<AlteracaoDispositivo> recognizeChanges(String content) {
		List<AlteracaoDispositivo> lista = new ArrayList<AlteracaoDispositivo>();
		for (Object key : dispositivos_modificadores.keySet()) {
			String[] regex = dispositivos_modificadores.get(key);
			for (String rule : regex) {
				if (content.matches(IGNORE_CASE_REGEX + "^.*\\s*" + rule + "\\s.*$")) {
					lista.add(new AlteracaoDispositivo(getTypeChange(content), getDispositivoChanged(content), getDataVigencia(content)));
				}
			}
		}
		return lista;
	}

	private String getDataVigencia(String trecho) {
		SimpleDateFormat formatterIn = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat formatterOut = new SimpleDateFormat("dd/MM/yyyy");
		String[] regex = { ".*\\s*Brasília,\\s(.*[0-9]{2}\\.*.[0-9])+" };
		String dateInStringIn = extractMatch(lexMLParser.getDataLocalFecho(), regex).replace("de ", "").replace("em ", "").trim();
		try {
			Date date = formatterIn.parse(dateInStringIn);
			return formatterOut.format(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private String getDispositivoChanged(String trecho) {
		String[] regex;
		String retorno = "";
		switch (getTypeChange(trecho)) {
		case "revogacao":
			regex = dispositivos_modificadores.get(getTypeChange(trecho));
			retorno = extractArtRevogacao(trecho, regex);
			break;
		case "novaredacao":
			regex = dispositivos_modificadores.get(getTypeChange(trecho));
			retorno = extractArtNovaRedacao(trecho, regex);
			break;
		}
		return retorno;
	}

	private String getTypeChange(String line) {
		for (Object key : dispositivos_modificadores.keySet()) {
			String[] regex = dispositivos_modificadores.get(key);
			for (String rule : regex) {
				if (line.matches(IGNORE_CASE_REGEX + "^.*\\s*" + rule + "\\s.*$")) {
					return key.toString();
				}
			}
		}
		return "";
	}

	private String extractMatch(String line, String[] regex) {
		for (String rule : regex) {
			Matcher matcher = Pattern.compile(IGNORE_CASE_REGEX + rule).matcher(line);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	private String extractArtRevogacao(String line, String[] regex) {
		for (String rule : regex) {
			Matcher matcher = Pattern.compile(IGNORE_CASE_REGEX + ".*\\s*" + rule + " (art\\.*.[0-9]+)").matcher(line);
			if (matcher.find()) {
				return matcher.group(1).replace(".", "").replace(" ", "");
			}
		}
		return null;
	}

	private String extractArtNovaRedacao(String line, String[] regex) {
		String prepare = null;
		for (String rule : regex) {
			Matcher matcher = Pattern.compile(IGNORE_CASE_REGEX + ".*\\s*.(inciso.\\s*.[A-Z]+).*\\s*(art\\.*.[0-9]+).*\\s*" + rule).matcher(line);
			if (matcher.find()) {
				prepare = matcher.group(2).replace(".", "").replace(" ", "");
				prepare += "_inc" + romanToDecimal(matcher.group(1).replace("inciso", ""));
			}
		}
		return prepare;
	}

	private int romanToDecimal(java.lang.String romanNumber) {
		int decimal = 0;
		int lastNumber = 0;
		String romanNumeral = romanNumber.toUpperCase();
		for (int x = romanNumeral.length() - 1; x >= 0; x--) {
			char convertToDecimal = romanNumeral.charAt(x);

			switch (convertToDecimal) {
			case 'M':
				decimal = processDecimal(1000, lastNumber, decimal);
				lastNumber = 1000;
				break;

			case 'D':
				decimal = processDecimal(500, lastNumber, decimal);
				lastNumber = 500;
				break;

			case 'C':
				decimal = processDecimal(100, lastNumber, decimal);
				lastNumber = 100;
				break;

			case 'L':
				decimal = processDecimal(50, lastNumber, decimal);
				lastNumber = 50;
				break;

			case 'X':
				decimal = processDecimal(10, lastNumber, decimal);
				lastNumber = 10;
				break;

			case 'V':
				decimal = processDecimal(5, lastNumber, decimal);
				lastNumber = 5;
				break;

			case 'I':
				decimal = processDecimal(1, lastNumber, decimal);
				lastNumber = 1;
				break;
			}
		}
		return decimal;
	}

	private int processDecimal(int decimal, int lastNumber, int lastDecimal) {
		if (lastNumber > decimal) {
			return lastDecimal - decimal;
		} else {
			return lastDecimal + decimal;
		}
	}

}
