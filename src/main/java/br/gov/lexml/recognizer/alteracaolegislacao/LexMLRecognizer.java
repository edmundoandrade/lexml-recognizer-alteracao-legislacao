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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import br.gov.lexml.parser.documentoarticulado.LexMLParser;

public class LexMLRecognizer {

	String[] TYPE_CHANGE = { "revogacao", "acrescimo", "novaredacao" };
	String[] REGEX_DISPOSITIVO_CHANGE = { "^.*\\s*Fica revogado o\\s.*$" };
	private final String IGNORE_CASE_REGEX = "(?i)";

	private LexMLParser lexMLParser;

	public LexMLRecognizer(LexMLParser lexMLParser) {
		this.lexMLParser = lexMLParser;
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
		for (String rule : REGEX_DISPOSITIVO_CHANGE) {
			if (content.matches(IGNORE_CASE_REGEX + rule)) {
				lista.add(new AlteracaoDispositivo(getTypeChange(content), getDispositivoChanged(content), getDataVigencia(content)));
			}
		}
		return lista;
	}

	private String getDataVigencia(String trecho) {
		SimpleDateFormat formatterIn = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat formatterOut = new SimpleDateFormat("dd/MM/yyyy");
		String[] regex = { ".*\\s*Brasília,\\s(.*[0-9]{2}\\.*.[0-9])+" };
		String dateInStringIn = extractMatch(lexMLParser.getDataLocalFecho(), regex).replace("de ", "");
		try {
			Date date = formatterIn.parse(dateInStringIn);
			return formatterOut.format(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private String getDispositivoChanged(String trecho) {
		String[] array = { ".*\\s*Fica revogado o (art\\.*.[0-9]+)" };
		return extractMatch(trecho, array).replace(".", "").replace(" ", "");
	}

	private String getTypeChange(String line) {
		int index = 0;
		for (String rule : REGEX_DISPOSITIVO_CHANGE) {
			if (line.matches(IGNORE_CASE_REGEX + rule)) {
				return TYPE_CHANGE[index];
			}
			index++;
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
}
