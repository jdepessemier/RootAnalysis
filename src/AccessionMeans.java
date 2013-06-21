public class AccessionMeans {
	
	private String name;
	private String concentration;
	private String box;
	private int N;
	private Double LPRmean;
	private Double NLRmean;
	private Double SLRLmean;
	private Double meanLRLmean;
	private Double DLRZ1mean;
	private Double DLRZ2mean;
	
	public AccessionMeans() {
		name = "";
		concentration = "";
		box = "";
		N = 0;
		LPRmean = 0.00;
		NLRmean = 0.00;
		SLRLmean = 0.00;
		meanLRLmean = 0.00;
		DLRZ1mean = 0.00;
		DLRZ2mean = 0.00;
	}
	
	public AccessionMeans(String name,
						  String concentration,
						  int nbOfPlants,
						  String box,
						  Double LPRmean,
						  Double NLRmean,
						  Double SLRLmean,
						  Double meanLRLmean,
						  Double DLRZ1mean,
						  Double DLRZ2mean) {
		this.name = name;
		this.concentration = concentration;
		this.N = nbOfPlants;
		this.box = box;
		this.LPRmean = LPRmean;
		this.NLRmean = NLRmean;
		this.SLRLmean = SLRLmean;
		this.meanLRLmean = meanLRLmean;
		this.DLRZ1mean = DLRZ1mean;
		this.DLRZ2mean = DLRZ2mean;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public String getConcentration(){
		return concentration;
	}

	public void setConcentration(String value) {
		concentration = value;
	}
	
	public String getBox(){
		return box;
	}

	public void setBox(String value) {
		box = value;
	}
	
	public int getN() {
		return N;
	}

	public void setN(int value) {
		N = value;
	}

	public Double getLPRmean() {
		return LPRmean;
	}

	public void setLPRmean(Double value) {
		LPRmean = value;
	}
	
	public Double getNLRmean() {
		return NLRmean;
	}

	public void setNLRmean(Double value) {
		NLRmean = value;
	}
	
	public Double getSLRLmean() {
		return SLRLmean;
	}

	public void setSLRLmean(Double value) {
		SLRLmean = value;
	}
	
	public Double getMeanLRLmean() {
		return meanLRLmean;
	}

	public void setMeanLRLmean(Double value) {
		meanLRLmean = value;
	}
	
	public Double getDLRZ1mean() {
		return DLRZ1mean;
	}

	public void setDLRZ1mean(Double value) {
		DLRZ1mean = value;
	}
	
	public Double getDLRZ2mean() {
		return DLRZ2mean;
	}

	public void setDLRZ2mean(Double value) {
		DLRZ2mean = value;
	}

}
	
