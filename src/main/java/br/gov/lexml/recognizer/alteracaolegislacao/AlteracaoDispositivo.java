package br.gov.lexml.recognizer.alteracaolegislacao;

public class AlteracaoDispositivo {
	private String type;
	private String dispositivoChanged;
	private String dataVigencia;

	public AlteracaoDispositivo(String type, String dispositivoChanged, String dataVigencia) {
		this.type = type;
		this.dispositivoChanged = dispositivoChanged;
		this.dataVigencia = dataVigencia;
	}

	@Override
	public String toString() {
		return type + " | " + dispositivoChanged + " | " + dataVigencia;
	}
}
