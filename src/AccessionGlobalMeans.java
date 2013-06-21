public class AccessionGlobalMeans {
	
	private String concentration;           // Accession Concentration	
	private Double LPRgmean;				// Length of Primary Root (LPR) global mean
	private Double NLRgmean;				// Number of Lateral Roots (NLR) global mean
	private Double SLRLgmean;				// Sum of Lateral Roots Length (SLRL) global mean
	private Double meanLRLgmean;				// Mean Lateral Roots Length (LRL) global mean
	private Double DLRZ1gmean;				// Density Lateral Roots Zone 1 (DLRZ1) global mean
	private Double DLRZ2gmean;				// Density Lateral Roots Zone 2 (DLRZ2) global mean
	
	public AccessionGlobalMeans() {
		concentration = "";
			
		LPRgmean = 0.00;
		NLRgmean = 0.00;
		SLRLgmean = 0.00;
		meanLRLgmean = 0.00;
		DLRZ1gmean = 0.00;
		DLRZ2gmean = 0.00;
	}
	
	public AccessionGlobalMeans(String concentration,
					 			Double lprgmean,
					 			Double nlrgmean,
					 			Double slrlgmean,
					 			Double meanlrlgmean,
					 			Double dlrz1gmean,
					 			Double dlrz2gmean) {
		this.concentration = concentration;
		this.LPRgmean = lprgmean;
		this.NLRgmean = nlrgmean;
		this.SLRLgmean = slrlgmean;
		this.meanLRLgmean = meanlrlgmean;
		this.DLRZ1gmean = dlrz1gmean;
		this.DLRZ2gmean = dlrz2gmean;
	}

	// Concentration
	
	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String value) {
		concentration = value;
	}

	// LPR global mean
	
	public Double getLPRgmean() {
		return LPRgmean;
	}

	public void setLPRgmean(Double value) {
		LPRgmean = value;
	}

	// NLR global mean
	
	public Double getNLRgmean() {
		return NLRgmean;
	}

	public void setNLRgmean(Double value) {
		NLRgmean = value;
	}

	// SLRL global mean
	
	public Double getSLRLgmean() {
		return SLRLgmean;
	}

	public void setSLRLgmean(Double value) {
		SLRLgmean = value;
	}

	// Mean LRL global mean
	
	public Double getMeanLRLgmean() {
		return meanLRLgmean;
	}

	public void setMeanLRLgmean(Double value) {
		meanLRLgmean = value;
	}
	
	// DLRZ1 global mean
	
	public Double getDLRZ1gmean() {
		return DLRZ1gmean;
	}

	public void setDLRZ1gmean(Double value) {
		DLRZ1gmean = value;
	}
	
	// DLRZ2 global mean
	
	public Double getDLRZ2gmean() {
		return DLRZ2gmean;
	}

	public void setDLRZ2gmean(Double value) {
		DLRZ2gmean = value;
	}
	
}
	
