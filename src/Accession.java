public class Accession {
	
	private String name;                    // Accession Name
	private String concentration;           // Accession Concentration
	private String box;                     // Accession box reference
	private int N;                          // Number of plants for the referenced box
	private Double[] LPR = new Double[5];   // Array of Length of Primary Roots
	private int[]    NLR = new int[5];      // Array of Number of Lateral Roots (>1mm)
	private Double[] SLRL = new Double[5];  // Array of Sum of Lateral Roots Length
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
	
	// Lateral Roots Length (LRL) mean, standard deviation, and standard error
	private Double LRLmean;
	private Double LRLsd;
	private Double LRLse;
	
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
			DLRZ1[i] = 0.00;
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
		LRLmean = 0.00;
		LRLsd = 0.00;
		LRLse = 0.00;
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
					 Double[] dlrz1,
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
					 Double lrlmean,
					 Double lrlsd,
					 Double lrlse,
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
		this.DLRZ1 = dlrz1;
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
		this.LRLmean = lrlmean;
		this.LRLsd = lrlsd;
		this.LRLse = lrlse;
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

	// DLRZ1
	
	public Double getDLRZ1(int idx) {
		return DLRZ1[idx];
	}

	public void setDLRZ1(double value, int idx) {
		DLRZ1[idx] = value;
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

	// LRL mean, sd, se
	
	public Double getLRLmean() {
		return LRLmean;
	}

	public void setLRLmean(Double value) {
		LRLmean = value;
	}

	public Double getLRLsd() {
		return LRLsd;
	}

	public void setLRLsd(Double value) {
		LRLsd = value;
	}

	public Double getLRLse() {
		return LRLse;
	}

	public void setLRLse(Double value) {
		LRLse = value;
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
	
