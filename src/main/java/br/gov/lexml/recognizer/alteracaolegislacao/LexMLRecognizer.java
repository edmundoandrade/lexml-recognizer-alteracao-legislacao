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
import java.util.List;

import org.w3c.dom.Element;

import br.gov.lexml.parser.documentoarticulado.LexMLParser;

public class LexMLRecognizer {

	String[] TYPE_CHANGE = { "revogado", "acrescimo", "novaredacao" };
	String[] REGEX_DISPOSITIVO_CHANGE = { "^.*\\s*Fica revogado o\\s.*$" };

	private LexMLParser lexMLParser;

	public LexMLRecognizer(LexMLParser lexMLParser) {
		this.lexMLParser = lexMLParser;
	}

	public List<String> getDispositivosModificadores() {
		List<String> deviceModify = new ArrayList<>();
		for (Element dispositivo : lexMLParser.getArtigos()) {
			// if (LexMlUtil.matches(line, REGEX_DISPOSITIVO_CHANGE)) {
			// deviceModify.add(line.replace("<p>", "").replace("</p>", ""));
			// }
		}
		return deviceModify;
	}
}
