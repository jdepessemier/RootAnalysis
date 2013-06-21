public class AccessionHighsLows {
	
	private String name;
	private String concentration;
	private String box;
	private Double LPRmean;
	private Double NLRmean;
	private Double SLRLmean;
	private Double meanLRLmean;	
	private Double DLRZ1mean;
	private Double DLRZ2mean;
	
	private Double LPRmeanlow;
	private Double NLRmeanlow;
	private Double SLRLmeanlow;
	private Double meanLRLmeanlow;
	private Double DLRZ1meanlow;
	private Double DLRZ2meanlow;
	
	private Double LPRmeanhigh;
	private Double NLRmeanhigh;
	private Double SLRLmeanhigh;
	private Double meanLRLmeanhigh;
	private Double DLRZ1meanhigh;
	private Double DLRZ2meanhigh;
	
	public AccessionHighsLows() {
		name = "";
		concentration = "";
		box = "";
		setLPRmean(0.00);
		setNLRmean(0.00);
		setSLRLmean(0.00);
		setMeanLRLmean(0.00);
		setDLRZ1mean(0.00);
		setDLRZ2mean(0.00);
		setLPRmeanlow(0.00);
		setNLRmeanlow(0.00);
		setSLRLmeanlow(0.00);
		setMeanLRLmeanlow(0.00);
		setDLRZ1meanlow(0.00);
		setDLRZ2meanlow(0.00);
		setLPRmeanhigh(0.00);
		setNLRmeanhigh(0.00);
		setSLRLmeanhigh(0.00);
		setMeanLRLmeanhigh(0.00);
		setDLRZ1meanhigh(0.00);
		setDLRZ2meanhigh(0.00);
	}
	
	public AccessionHighsLows(String name,
						  String concentration,
						  String box,
						  Double LPRmean,
						  Double NLRmean,
						  Double SLRLmean,
						  Double meanLRLmean,
						  Double DLRZ1mean,
						  Double DLRZ2mean,
						  Double LPRmeanlow,
						  Double NLRmeanlow,
						  Double SLRLmeanlow,
						  Double meanLRLmeanlow,
						  Double DLRZ1meanlow,
						  Double DLRZ2meanlow,
						  Double LPRmeanhigh,
						  Double NLRmeanhigh,
						  Double SLRLmeanhigh,
						  Double meanLRLmeanhigh,
						  Double DLRZ1meanhigh,
						  Double DLRZ2meanhigh) {
		this.name = name;
		this.concentration = concentration;
		this.box = box;
		this.setLPRmean(LPRmean);
		this.setNLRmean(NLRmean);
		this.setSLRLmean(SLRLmean);
		this.setMeanLRLmean(meanLRLmean);
		this.setDLRZ1mean(DLRZ1mean);
		this.setDLRZ2mean(DLRZ1mean);
		this.setLPRmeanlow(LPRmeanlow);
		this.setNLRmeanlow(NLRmeanlow);
		this.setSLRLmeanlow(SLRLmeanlow);
		this.setMeanLRLmeanlow(meanLRLmeanlow);
		this.setDLRZ1meanlow(DLRZ1meanlow);
		this.setDLRZ2meanlow(DLRZ1meanlow);
		this.setLPRmeanhigh(LPRmeanhigh);
		this.setNLRmeanhigh(NLRmeanhigh);
		this.setSLRLmeanhigh(SLRLmeanhigh);
		this.setMeanLRLmeanhigh(meanLRLmeanhigh);
		this.setDLRZ1meanhigh(DLRZ1meanhigh);
		this.setDLRZ2meanhigh(DLRZ1meanhigh);
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

	public Double getLPRmean() {
		return LPRmean;
	}

	public void setLPRmean(Double lPRmean) {
		LPRmean = lPRmean;
	}

	public Double getNLRmean() {
		return NLRmean;
	}

	public void setNLRmean(Double nLRmean) {
		NLRmean = nLRmean;
	}

	public Double getSLRLmean() {
		return SLRLmean;
	}

	public void setSLRLmean(Double sLRLmean) {
		SLRLmean = sLRLmean;
	}

	public Double getMeanLRLmean() {
		return meanLRLmean;
	}

	public void setMeanLRLmean(Double meanLRLmean) {
		this.meanLRLmean = meanLRLmean;
	}

	public Double getDLRZ1mean() {
		return DLRZ1mean;
	}

	public void setDLRZ1mean(Double dLRZ1mean) {
		DLRZ1mean = dLRZ1mean;
	}

	public Double getDLRZ2mean() {
		return DLRZ2mean;
	}

	public void setDLRZ2mean(Double dLRZ2mean) {
		DLRZ2mean = dLRZ2mean;
	}

	public Double getLPRmeanlow() {
		return LPRmeanlow;
	}

	public void setLPRmeanlow(Double lPRmeanlow) {
		LPRmeanlow = lPRmeanlow;
	}

	public Double getNLRmeanlow() {
		return NLRmeanlow;
	}

	public void setNLRmeanlow(Double nLRmeanlow) {
		NLRmeanlow = nLRmeanlow;
	}

	public Double getSLRLmeanlow() {
		return SLRLmeanlow;
	}

	public void setSLRLmeanlow(Double sLRLmeanlow) {
		SLRLmeanlow = sLRLmeanlow;
	}

	public Double getMeanLRLmeanlow() {
		return meanLRLmeanlow;
	}

	public void setMeanLRLmeanlow(Double meanLRLmeanlow) {
		this.meanLRLmeanlow = meanLRLmeanlow;
	}

	public Double getDLRZ1meanlow() {
		return DLRZ1meanlow;
	}

	public void setDLRZ1meanlow(Double dLRZ1meanlow) {
		DLRZ1meanlow = dLRZ1meanlow;
	}

	public Double getDLRZ2meanlow() {
		return DLRZ2meanlow;
	}

	public void setDLRZ2meanlow(Double dLRZ2meanlow) {
		DLRZ2meanlow = dLRZ2meanlow;
	}

	public Double getLPRmeanhigh() {
		return LPRmeanhigh;
	}

	public void setLPRmeanhigh(Double lPRmeanhigh) {
		LPRmeanhigh = lPRmeanhigh;
	}

	public Double getNLRmeanhigh() {
		return NLRmeanhigh;
	}

	public void setNLRmeanhigh(Double nLRmeanhigh) {
		NLRmeanhigh = nLRmeanhigh;
	}

	public Double getSLRLmeanhigh() {
		return SLRLmeanhigh;
	}

	public void setSLRLmeanhigh(Double sLRLmeanhigh) {
		SLRLmeanhigh = sLRLmeanhigh;
	}

	public Double getMeanLRLmeanhigh() {
		return meanLRLmeanhigh;
	}

	public void setMeanLRLmeanhigh(Double meanLRLmeanhigh) {
		this.meanLRLmeanhigh = meanLRLmeanhigh;
	}

	public Double getDLRZ1meanhigh() {
		return DLRZ1meanhigh;
	}

	public void setDLRZ1meanhigh(Double dLRZ1meanhigh) {
		DLRZ1meanhigh = dLRZ1meanhigh;
	}

	public Double getDLRZ2meanhigh() {
		return DLRZ2meanhigh;
	}

	public void setDLRZ2meanhigh(Double dLRZ2meanhigh) {
		DLRZ2meanhigh = dLRZ2meanhigh;
	}

	

}
