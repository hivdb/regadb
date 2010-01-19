package net.sf.regadb.ui.form.importTool.data;

public class SequenceDetails {
	public enum SequenceRetrievalOptions {
		CSV("Sequence is embedded in the Excell file"),
		FASTA("Retrieve sequence from external Fasta file");
		
		private SequenceRetrievalOptions(String text) {
			this.text = text;
		}
		
		private String text;

		public String getText() {
			return text;
		}
	}
	
	private SequenceRetrievalOptions retrievalOptions;

	public SequenceRetrievalOptions getRetrievalOptions() {
		return retrievalOptions;
	}

	public void setRetrievalOptions(SequenceRetrievalOptions retrieval) {
		this.retrievalOptions = retrieval;
	}
}
