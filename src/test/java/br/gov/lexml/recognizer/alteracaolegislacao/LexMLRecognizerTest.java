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

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.lexml.parser.documentoarticulado.LexMLParserFromText;

public class LexMLRecognizerTest {

	@Ignore
	public void detectDispositivosRevogadores() {
		LexMLRecognizer recognizerLei9792 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 9792-1999.utf-8.txt")));
		List<String> dispositivosModificadoresLei9792 = recognizerLei9792.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresLei9792, CoreMatchers.hasItem("revogacao | art112 | 14/04/1999"));
	}

	@Test
	@Ignore
	public void detectDispositivosAlteradores() {
		LexMLRecognizer recognizerEmenda28 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/EMENDA-CONSTITUCIONAL-Nº 28-2000.utf-8.txt")));
		List<String> dispositivosModificadoresEmenda28 = recognizerEmenda28.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresEmenda28, CoreMatchers.hasItem("novaredacao | art7_inc29 | 25/05/2000"));
	}

	private String sampleText(String resourceName) {
		return TestUtil.sampleText(resourceName);
	}
}
