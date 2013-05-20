public class Accession {
	
	private String name;                    // Accession Name
	private String concentration;           // Accession Concentration
	private String box;                     // Accession box reference
	private int N;                          // Number of plants for the referenced box
	private Double[] LPR = new Double[5];   // Array of Length of Primary Roots
	private int[]    NLR = new int[5];      // Array of Number of Lateral Roots (>1mm)
	private Double[] SLRL = new Double[5];  // Array of Sum of Lateral Roots Length
	private Double[] meanLRL = new Double[5];  // Array of mean Lateral Roots Length
	private Double[] P1_Plast = new Double[5];  // Array of Length between first and last Lateral Roots positions
	private Double[] DLRZ1 = new Double[5]; // Array of Density of Lateral Roots (Z1)
	private Double[] DLRZ2 = new Double[5]; // Array of Density of Lateral Roots (Z2)
	
	// Length of Primary Root (LPR) mean, standard deviation, and standard error
	private Double LPRmean;
	private Double LPRsd;
	private Double LPRse;
	
	// Number of Lateral Roots (NLR) mean, standard deviation, and standard error 
	private Double NLRmean;
	private Double NLRsd;
	private Double NLRse;
	
	// Sum of Lateral Roots Length (SLRL) mean, standard deviation, and standard error
	private Double SLRLmean;
	private Double SLRLsd;
	private Double SLRLse;
	
	// Mean Lateral Roots Length (LRL) mean, standard deviation, and standard error
	private Double meanLRLmean;
	private Double meanLRLsd;
	private Double meanLRLse;
	
	// Density Lateral Roots Zone 1 (DLRZ1) mean, standard deviation, and standard error
	private Double DLRZ1mean;
	private Double DLRZ1sd;
	private Double DLRZ1se;
	
	// Density Lateral Roots Zone 2 (DLRZ2) mean, standard deviation, and standard error
	private Double DLRZ2mean;
	private Double DLRZ2sd;
	private Double DLRZ2se;
	
	public Accession() {
		name = "";
		concentration = "";
		box = "";
		N = 0;
		
		for (int i=0; i<4; i++) {
			LPR[i] = 0.00;
			NLR[i] = 0;
			SLRL[i] = 0.00;
			meanLRL[i] = 0.00;
			DLRZ1[i] = 0.00;
			P1_Plast[i] = 0.00;
			DLRZ2[i] = 0.00;
		}
		
		LPRmean = 0.00;
		LPRsd = 0.00;
		LPRse = 0.00;
		NLRmean = 0.00;
		NLRsd = 0.00;
		NLRse = 0.00;
		SLRLmean = 0.00;
		SLRLsd = 0.00;
		SLRLse = 0.00;	
		meanLRLmean = 0.00;
		meanLRLsd = 0.00;
		meanLRLse = 0.00;
		DLRZ1mean = 0.00;
		DLRZ1sd = 0.00;
		DLRZ1se = 0.00;
		DLRZ2mean = 0.00;
		DLRZ2sd = 0.00;
		DLRZ2se = 0.00;
	}
	
	public Accession(String name,
					 String concentration,
					 String box,
					 int nbofplants,
					 Double[] lpr,
					 int[] nlr,
					 Double[] slrl,
					 Double[] meanlrl,
					 Double[] dlrz1,
					 Double[] p1_plast,
					 Double[] dlrz2,
					 Double lprmean,
					 Double lprsd,
					 Double lprse,
					 Double nlrmean,
					 Double nlrsd,
					 Double nlrse,
					 Double slrlmean,
					 Double slrlsd,
					 Double slrlse,
					 Double meanlrlmean,
					 Double meanlrlsd,
					 Double meanlrlse,
					 Double dlrz1mean,
					 Double dlrz1sd,
					 Double dlrz1se,
					 Double dlrz2mean,
					 Double dlrz2sd,
					 Double dlrz2se) {
		this.name = name;
		this.concentration = concentration;
		this.box = box;
		this.N = nbofplants;
		this.LPR = lpr;
		this.NLR = nlr;
		this.SLRL = slrl;
		this.meanLRL = meanlrl;
		this.DLRZ1 = dlrz1;
		this.P1_Plast = p1_plast;
		this.DLRZ2 = dlrz2;
		this.LPRmean = lprmean;
		this.LPRsd = lprsd;
		this.LPRse = lprse;
		this.NLRmean = nlrmean;
		this.NLRsd = nlrsd;
		this.NLRse = nlrse;
		this.SLRLmean = slrlmean;
		this.SLRLsd = slrlsd;
		this.SLRLse = slrlse;
		this.meanLRLmean = meanlrlmean;
		this.meanLRLsd = meanlrlsd;
		this.meanLRLse = meanlrlse;
		this.DLRZ1mean = dlrz1mean;
		this.DLRZ1sd = dlrz1sd;
		this.DLRZ1se = dlrz1se;
		this.DLRZ2mean = dlrz2mean;
		this.DLRZ2sd = dlrz2sd;
		this.DLRZ2se = dlrz2se;
	}

	// Accession Name
	
	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	// Concentration
	
	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String value) {
		concentration = value;
	}

	// Box
	
	public String getBox() {
		return box;
	}

	public void setBox(String value) {
		box = value;
	}

	// Number of Plants
	
	public int getN() {
		return N;
	}

	public void setN(int value) {
		N = value;
	}
	
	// LPR
	
	public Double getLPR(int idx) {
		return LPR[idx];
	}

	public void setLPR(double value, int idx) {
		LPR[idx] = value;
	}
	
	// NLR
	
	public int getNLR(int idx) {
		return NLR[idx];
	}

	public void setNLR(int value, int idx) {
		NLR[idx] = value;
	}
	
	// SLRL
	
	public Double getSLRL(int idx) {
		return SLRL[idx];
	}

	public void setSLRL(double value, int idx) {
		SLRL[idx] = value;
	}

	// Mean LRL
	
	public Double getMeanLRL(int idx) {
		return meanLRL[idx];
	}

	public void setMeanLRL(double value, int idx) {
		meanLRL[idx] = value;
	}
	
	// DLRZ1
	
	public Double getDLRZ1(int idx) {
		return DLRZ1[idx];
	}

	public void setDLRZ1(double value, int idx) {
		DLRZ1[idx] = value;
	}
	
	// P1_Plast
	
	public Double getP1_Plast(int idx) {
		return P1_Plast[idx];
	}

	public void setP1_Plast(double value, int idx) {
		P1_Plast[idx] = value;
	}
	
	// DLRZ2
	
	public Double getDLRZ2(int idx) {
		return DLRZ2[idx];
	}

	public void setDLRZ2(double value, int idx) {
		DLRZ2[idx] = value;
	}

	// LPR mean, sd, se
	
	public Double getLPRmean() {
		return LPRmean;
	}

	public void setLPRmean(Double value) {
		LPRmean = value;
	}

	public Double getLPRsd() {
		return LPRsd;
	}

	public void setLPRsd(Double value) {
		LPRsd = value;
	}

	public Double getLPRse() {
		return LPRse;
	}

	public void setLPRse(Double value) {
		LPRse = value;
	}

	// NLR mean, sd, se
	
	public Double getNLRmean() {
		return NLRmean;
	}

	public void setNLRmean(Double value) {
		NLRmean = value;
	}

	public Double getNLRsd() {
		return NLRsd;
	}

	public void setNLRsd(Double value) {
		NLRsd = value;
	}

	public Double getNLRse() {
		return NLRse;
	}

	public void setNLRse(Double value) {
		NLRse = value;
	}

	// SLRL mean, sd, se
	
	public Double getSLRLmean() {
		return SLRLmean;
	}

	public void setSLRLmean(Double value) {
		SLRLmean = value;
	}

	public Double getSLRLsd() {
		return SLRLsd;
	}

	public void setSLRLsd(Double value) {
		SLRLsd = value;
	}

	public Double getSLRLse() {
		return SLRLse;
	}

	public void setSLRLse(Double value) {
		SLRLse = value;
	}

	// Mean LRL mean, sd, se
	
	public Double getMeanLRLmean() {
		return meanLRLmean;
	}

	public void setMeanLRLmean(Double value) {
		meanLRLmean = value;
	}

	public Double getMeanLRLsd() {
		return meanLRLsd;
	}

	public void setMeanLRLsd(Double value) {
		meanLRLsd = value;
	}

	public Double getMeanLRLse() {
		return meanLRLse;
	}

	public void setMeanLRLse(Double value) {
		meanLRLse = value;
	}
	
	// DLRZ1 mean, sd, se
	
	public Double getDLRZ1mean() {
		return DLRZ1mean;
	}

	public void setDLRZ1mean(Double value) {
		DLRZ1mean = value;
	}

	public Double getDLRZ1sd() {
		return DLRZ1sd;
	}

	public void setDLRZ1sd(Double value) {
		DLRZ1sd = value;
	}

	public Double getDLRZ1se() {
		return DLRZ1se;
	}

	public void setDLRZ1se(Double value) {
		DLRZ1se = value;
	}
	
	// DLRZ2 mean, sd, se
	
	public Double getDLRZ2mean() {
		return DLRZ2mean;
	}

	public void setDLRZ2mean(Double value) {
		DLRZ2mean = value;
	}

	public Double getDLRZ2sd() {
		return DLRZ2sd;
	}

	public void setDLRZ2sd(Double value) {
		DLRZ2sd = value;
	}

	public Double getDLRZ2se() {
		return DLRZ2se;
	}

	public void setDLRZ2se(Double value) {
		DLRZ2se = value;
	}
	
}
	
