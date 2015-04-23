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
	public void detectDispositivosRevogacao() throws ParseException {
		LexMLRecognizer recognizerLei9792 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 9792-1999.utf-8.txt")));
		List<String> dispositivosModificadoresLei9792 = recognizerLei9792.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresLei9792, CoreMatchers.hasItem("revogacao | art112 | 14/04/1999"));
		LexMLRecognizer recognizerLei6533 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 6533-1978.utf-8.txt")));
		List<String> dispositivosModificadoresLei6533 = recognizerLei6533.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art35 | 19/08/1978"));
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art480_par2 | 19/08/1978"));
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art507_par1 | 19/08/1978"));
		Assert.assertThat(dispositivosModificadoresLei6533, CoreMatchers.hasItem("revogacao | art509 | 19/08/1978"));
	}

	@Test
	public void detectDispositivosNovaRedacao() throws ParseException {
		LexMLRecognizer recognizerLei8921 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 8921-1994.utf-8.txt")));
		List<String> dispositivosModificadoresLei8921 = recognizerLei8921.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresLei8921, CoreMatchers.hasItem("novaredacao | art131_cpt_inc2 | 25/07/1994"));
	}

	@Test
	public void detectDispositivosAcrescimoAndNovaredaco() throws ParseException {
		LexMLRecognizer recognizerEmenda852015 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/EMENDA-CONSTITUCIONAL-Nº 85-2015.utf-8.txt")));
		List<String> dispositivosModificadoresEmenda852015 = recognizerEmenda852015.getDispositivosModificadores();
		Assert.assertEquals(15, dispositivosModificadoresEmenda852015.size());
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art23_cpt_inc5 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art24_cpt_inc9 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art167_par5 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art200_cpt_inc5 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art213_par2 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art218_cpt | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art218_par1 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art218_par3 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art218_par6 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art218_par7 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("novaredacao | art219_par1u | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("acrescimo | art219-1_cpt | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("acrescimo | art219-2_cpt | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("acrescimo | art219-2_par1 | 27/02/2015"));
		Assert.assertThat(dispositivosModificadoresEmenda852015, CoreMatchers.hasItem("acrescimo | art219-2_par2 | 27/02/2015"));
	}

	@Test
	public void detectDispositivosRevogacaoAndAcrescimo() throws ParseException {
		LexMLRecognizer recognizerLei7033 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 7033-1982.utf-8.txt")));
		List<String> dispositivosModificadoresEmenda7033 = recognizerLei7033.getDispositivosModificadores();
		Assert.assertThat(dispositivosModificadoresEmenda7033, CoreMatchers.hasItem("revogacao | art899_par3 | 06/10/1982"));
		Assert.assertThat(dispositivosModificadoresEmenda7033, CoreMatchers.hasItem("revogacao | art902 | 06/10/1982"));
		Assert.assertThat(dispositivosModificadoresEmenda7033, CoreMatchers.hasItem("novaredacao | art702_cpt_inc1_ali6 | 06/10/1982"));
		Assert.assertThat(dispositivosModificadoresEmenda7033, CoreMatchers.hasItem("novaredacao | art894_cpt_ali2 | 06/10/1982"));
		Assert.assertThat(dispositivosModificadoresEmenda7033, CoreMatchers.hasItem("novaredacao | art896_cpt_ali1 | 06/10/1982"));
		Assert.assertThat(dispositivosModificadoresEmenda7033, CoreMatchers.hasItem("novaredacao | art9_cpt | 06/10/1982"));
	}

	@Test
	public void detectDispositivosNovaRedacaoAndRevogacaoAndAcrescimo() throws ParseException {
		LexMLRecognizer recognizerLei8883 = new LexMLRecognizer(new LexMLParserFromText(sampleText("/input/IN-DOU-Lei 8883-1994.utf-8.txt")));
		List<String> dispositivosModificadoresLei8883 = recognizerLei8883.getDispositivosModificadores();
		Assert.assertEquals(103, dispositivosModificadoresLei8883.size());
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art3_par1_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art3_par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art6_cpt_inc8 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art6_cpt_inc8_ali3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art6_cpt_inc13 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par1u | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art9_par3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | inc2_ali3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par1u | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par1u_inc1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par1u_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par1u_inc3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art12_cpt | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art12_cpt_inc6 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | inc3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | inc8 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | par1u | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art17_cpt_inc1_ali5 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art17_cpt_inc1_ali6 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art17_par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art17_par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art17_par5 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art17_par6 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art19_cpt_inc3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art21_cpt | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art21_cpt_inc1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art21_cpt_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("novaredacao | art21_cpt_inc3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par1_inc1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par1_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par1_inc2_ali1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par1_inc2_ali2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par7 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par7_inc1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par7_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par8 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par9 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par10 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par11 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art30_par12 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par5 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par6 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art32_cpt | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par1u | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art39_par1u | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_cpt_inc10 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_cpt_inc11 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_cpt_inc12 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_cpt_inc14_ali1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_cpt_inc14_ali3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_par2_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_par4_inc1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art40_par4_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art41_par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art42_par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art42_par5 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art43_par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art45_par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art45_par1_inc4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art45_par3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art45_par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art48_cpt_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art48_par1u | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art53_par3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art53_par4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art55_par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par1_inc1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par1_inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par1_inc3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | par3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | inc2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | inc3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art62_par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art65_cpt_inc2_ali4 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art71_par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art71_par2 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("acrescimo | art71_par3 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("revogacao | art18_par1 | 09/06/1994"));
		Assert.assertThat(dispositivosModificadoresLei8883, CoreMatchers.hasItem("revogacao | art18 | 09/06/1994"));
	}

	private String sampleText(String resourceName) {
		return TestUtil.sampleText(resourceName);
	}
}
