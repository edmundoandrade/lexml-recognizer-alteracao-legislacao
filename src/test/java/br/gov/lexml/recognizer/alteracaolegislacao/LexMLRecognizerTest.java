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
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import br.gov.lexml.parser.documentoarticulado.LexMLParserFromText;

public class LexMLRecognizerTest {

	@Test
	public void detectDispositivosRevogadores() throws ParseException {
		LexMLRecognizer recognizerLei9792 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 9792-1999.utf-8.txt")));
		List<String> dispositivosModificadoresLei9792 = recognizerLei9792.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresLei9792, CoreMatchers.hasItem("revogacao | art112 | 14/04/1999"));
	}

	@Test
	public void detectDispositivosAlteradoresA() throws ParseException {
		LexMLRecognizer recognizerLei8921 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 8921-1994.utf-8.txt")));
		List<String> dispositivosModificadoresLei8921 = recognizerLei8921.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresLei8921, CoreMatchers.hasItem("novaredacao | art131_inc2 | 25/07/1994"));
	}

	@Test
	public void detectDispositivosAlteradoresB() throws ParseException {
		LexMLRecognizer recognizerLei6533 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 6533-1978.utf-8.txt")));
		List<String> dispositivosModificadoresLei6533 = recognizerLei6533.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art35 | 19/08/1978"));
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art480 | 19/08/1978"));
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art507 | 19/08/1978"));
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art509 | 19/08/1978"));
	}
	
	@Test
	public void detectDispositivosAlteradoresC() throws ParseException {
		LexMLRecognizer recognizerEmenda852015 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/EMENDA-CONSTITUCIONAL-Nº 85-2015.utf-8.txt")));
		List<String> dispositivosModificadoresEmenda852015= recognizerEmenda852015.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art23 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art24 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art167 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art200 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art213 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art218 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art219 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("acrescimo | art219-A | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("acrescimo | art219-B | 27/02/2015"));
	}

	private String sampleText(String resourceName) {
		return TestUtil.sampleText(resourceName);
	}
}
