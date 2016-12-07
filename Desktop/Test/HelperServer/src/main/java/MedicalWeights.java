import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 6/4/16.
 * MedicalWeights(Medical Weights) is used whenever we use 'magical' formulas to give weights to things.
 * This way we know every instance in which we bullshitted a weight value.
 * This class could possibly be broken up into many classes in the future.
 */

public class MedicalWeights {

    public static double qThresh = 2.0;
    public MedicalWeights(){
        initializeHashMap();
    }

    //Semantic weights

    //David Weighted (resetted)
        public float ortf = 1f;  //organ or tissue function
        public float fndg = .2f;   //finding
        public float ftcn = 0f;    //functional group
        public float dsyn = 1f;  //Disease or Syndrome
        public float bpoc = 1f;  //body part or organ
        public float tisu = 1f;  //tissue
        public float bdsu = 1f;  //Body Substance
        public float patf = 1f;   //Pathologic Function
        public float anab = 1f;  //Anatomical Abnormality
        public float neop = 1f;  //Neoplastic Process
        public float geoa = .2f;	    //geographic area
        public float emst = 1f;    //embryonic structure
        public float phsu = 1f;     //pharmacological substance
        public float diap = 1f;    //diagnostic procedure
        public float sbst = 1f;     //substance
        public float inpr = .3f; 	//intellectual product
        public float topp = 1f;	    //Therapeutic or Preventive Procedure

    //DownWeighted


    //Jason weighted

        //These items are weighted down for temporary convenience. It should be weighted according to the sources classification.
        public float fish = .8f;		//Fish*
        public float medd = .8f;	    //medical device
        public float sosy = .8f;   //sign or symptom

        //Total and complete shutdown of
        public static float idcn = -1f;		//Idea or Concept*
        public static float tmco = -1f; 	//temporal process
        public static float qlco = -1f;	    //qualitative concept
        public static float qnco = -1f; 	//quantitative concept
        public static float spco = -1f;	    //spacial concept
        public static float resa = -1f;		//Research Activity*
        public static float prog = -1f;		//Professional or Occupational Group*
        public static float acty = -1f;		//Activity*
        public static float cnce = -1f;		//Conceptual Entity*
        public static float clas = -1f;		//Classification*
        public static float ocac = -1f;		//Occupational Activity*
        public static float popg = -1f;		//Population Group*
        public static float famg = -1f;		//Family Group*
        public static float mnob = -1f;	    //manufactured object
        public static float aggp = -1f;     //age group
        public static String[] ignoreSems = {"idcn","tmco","qlco","qnco","spco","resa","prog","acty","cnce","clas","ocac","popg","famg","mnob","aggp"};


        //Banned semtypes that could be revived with a good complexity detection system
        public float inbe = .6f;		//Individual Behavior*
        public float menp = .6f;		//Mental Process*

        //Under scruitiny
        public float orgt = 1f;		//Organization*


    //Still relatively unknown
    public float aapp = 1f;		//Amino Acid, Peptide, or Protein*
    public float acab = 1f;		//Acquired Abnormality*
    public float amas = 1f;		//Amino Acid Sequence*
    public float amph = 1f;		//Amphibian*
    public float anim = 1f;		//Animal*
    public float anst = 1f;		//Anatomical Structure*
    public float antb = 1f;		//Antibiotic*
    public float arch = 1f;		//Archaeon*
    public float bacs = 1f;		//Biologically Active Substance*
    public float bact = 1f;		//Bacterium*
    public float bdsy = 1f;		//Body System*
    public float bhvr = 1f;		//Behavior*
    public float biof = 1f;		//Biologic Function*
    public float bird = 1f;		//Bird*
    public float blor = 1f;		//Body Location or Region*
    public float bmod = 1f;		//Biomedical Occupation or Discipline*
    public float bodm = 1f;		//Biomedical or Dental Material*
    public float bsoj = 1f;		//Body Space or Junction*
    public float carb = 1f;		//Carbohydrate*
    public float celc = 1f;		//Cell Component*
    public float celf = 1f;		//Cell Function*
    public float cell = 1f;		//Cell*
    public float cgab = 1f;		//Congenital Abnormality*
    public float chem = 1f;		//Chemical*
    public float chvf = 1f;		//Chemical Viewed Functionally*
    public float chvs = 1f;		//Chemical Viewed Structurally*
    public float clna = 1f;		//Clinical Attribute*
    public float clnd = 1f;		//Clinical Drug*
    public float comd = 1f;		//Cell or Molecular Dysfunction*
    public float crbs = 1f;		//Carbohydrate Sequence*
    public float dora = 1f;		//Daily or Recreational Activity*
    public float drdd = 1f;		//Drug Delivery Device*
    public float edac = 1f;		//Educational Activity*
    public float eehu = 1f;		//Environmental Effect of Humans*
    public float eico = 1f;		//Eicosanoid*
    public float elii = 1f;		//Element, Ion, or Isotope*
    public float emod = 1f;		//Experimental Model of Disease*
    public float enty = 1f;		//Entity*
    public float enzy = 1f;		//Enzyme*
    public float euka = 1f;		//Eukaryote*
    public float evnt = 1f;		//Event*
    public float ffas = 1f;		//Fully Formed Anatomical Structure*
    public float fngs = 1f;		//Fungus*
    public float food = 1f;		//Food*
    public float genf = 1f;		//Genetic Function*
    public float gngm = 1f;		//Gene or Genome*
    public float gora = 1f;		//Governmental or Regulatory Activity*
    public float grpa = 1f;		//Group Attribute*
    public float grup = 1f;		//Group*
    public float hcpp = 1f;		//Human-caused Phenomenon or Process*
    public float hcro = 1f;		//Health Care Related Organization*
    public float hlca = 1f;		//Health Care Activity*
    public float hops = 1f;		//Hazardous or Poisonous Substance*
    public float horm = 1f;		//Hormone*
    public float humn = 1f;		//Human*
    public float imft = 1f;		//Immunologic Factor*
    public float inch = 1f;		//Inorganic Chemical*
    public float inpo = 1f;		//Injury or Poisoning*
    public float irda = 1f;		//Indicator, Reagent, or Diagnostic Aid*
    public float lang = 1f;		//Language*
    public float lbpr = 1f;		//Laboratory Procedure*
    public float lbtr = 1f;		//Laboratory or Test Result*
    public float lipd = 1f;		//Lipid*
    public float mamm = 1f;		//Mammal*
    public float mbrt = 1f;		//Molecular Biology Research Technique*
    public float mcha = 1f;		//Machine Activity*
    public float mobd = 1f;		//Mental or Behavioral Dysfunction*
    public float moft = 1f;		//Molecular Function*
    public float mosq = 1f;		//Molecular Sequence*
    public float nnon = 1f;		//Nucleic Acid, Nucleoside, or Nucleotide*
    public float npop = 1f;		//Natural Phenomenon or Process*
    public float nsba = 1f;		//Neuroreactive Substance or Biogenic Amine*
    public float nusq = 1f;		//Nucleotide Sequence*
    public float ocdi = 1f;		//Occupation or Discipline*
    public float opco = 1f;		//Organophosphorus Compound*
    public float orch = 1f;		//Organic Chemical*
    public float orga = 1f;		//Organism Attribute*
    public float orgf = 1f;		//Organism Function*
    public float orgm = 1f;		//Organism*
    public float phob = 1f;		//Physical Object*
    public float phpr = 1f;		//Phenomenon or Process*
    public float phsf = 1f;		//Physiologic Function*
    public float plnt = 1f;		//Plant*
    public float podg = 1f;		//Patient or Disabled Group*
    public float pros = 1f;		//Professional Society*
    public float rcpt = 1f;		//Receptor*
    public float rept = 1f;		//Reptile*
    public float resd = 1f;		//Research Device*
    public float rnlw = 1f;		//Regulation or Law*
    public float shro = 1f;		//Self-help or Relief Organization*
    public float socb = 1f;		//Social Behavior*
    public float strd = 1f;		//Steroid*
    public float virs = 1f;		//Virus*
    public float vita = 1f;		//Vitamin*
    public float vtbt = 1f;		//Vertebrate

    //Custom semTypes
    public float date = 1.2f;
    public float perc = 1.1f;
    public float nums = 1.1f;
    public float time = 1.2f;

    public float[] semWeights = {ortf,fndg,ftcn,aggp,sosy,dsyn,bpoc,tisu,bdsu,patf,anab,neop,tmco,qlco,qnco,spco,geoa,emst,phsu,diap,sbst,mnob,inpr,medd,topp,aapp,acab,acty,amas,amph,anim,anst,antb,arch,bacs,bact,bdsy,bhvr,biof,bird,blor,bmod,bodm,bsoj,carb,celc,celf,cell,cgab,chem,chvf,chvs,clas,clna,clnd,cnce,comd,crbs,dora,drdd,edac,eehu,eico,elii,emod,enty,enzy,euka,evnt,famg,ffas,fish,fngs,food,genf,gngm,gora,grpa,grup,hcpp,hcro,hlca,hops,horm,humn,idcn,imft,inbe,inch,inpo,irda,lang,lbpr,lbtr,lipd,mamm,mbrt,mcha,menp,mobd,moft,mosq,nnon,npop,nsba,nusq,ocac,ocdi,opco,orch,orga,orgf,orgm,orgt,phob,phpr,phsf,plnt,podg,popg,prog,pros,rcpt,rept,resa,resd,rnlw,shro,socb,strd,virs,vita,vtbt,date,perc,nums};
    public String[] semLabel = {"ortf","fndg","ftcn","aggp","sosy","dsyn","bpoc","tisu","bdsu","patf","anab","neop","tmco","qlco","qnco","spco","geoa","emst","phsu","diap","sbst","mnob","inpr","medd","topp","aapp","acab","acty","amas","amph","anim","anst","antb","arch","bacs","bact","bdsy","bhvr","biof","bird","blor","bmod","bodm","bsoj","carb","celc","celf","cell","cgab","chem","chvf","chvs","clas","clna","clnd","cnce","comd","crbs","dora","drdd","edac","eehu","eico","elii","emod","enty","enzy","euka","evnt","famg","ffas","fish","fngs","food","genf","gngm","gora","grpa","grup","hcpp","hcro","hlca","hops","horm","humn","idcn","imft","inbe","inch","inpo","irda","lang","lbpr","lbtr","lipd","mamm","mbrt","mcha","menp","mobd","moft","mosq","nnon","npop","nsba","nusq","ocac","ocdi","opco","orch","orga","orgf","orgm","orgt","phob","phpr","phsf","plnt","podg","popg","prog","pros","rcpt","rept","resa","resd","rnlw","shro","socb","strd","virs","vita","vtbt","date","perc","nums"};
    public HashMap<String, Float> semValue; //HashMap of semType string to weight value


    public static String getIgnores(){
        String out = " -k ";
        for(int i=0; i<ignoreSems.length;i++){
                out+=ignoreSems[i]+",";
        }
        return out.substring(0,out.length()-1);
    }

    //Returns the semValue HashMap
    public HashMap<String, Float> getSemValue(){
        if(semValue == null){
            initializeHashMap();
        }
        return semValue;
    }

    //Initializes the semValue HashMap
    public void initializeHashMap(){
        semValue = new HashMap<String,Float>();
        for(int i = 0; i< semWeights.length; i++){
            semValue.put(semLabel[i], semWeights[i]);
        }
    }

    //ReWeights the semValues so that more frequent sems in the document get weighted higher.
    public float semFreqBonusMag = 3; //Magnitude on how important semFrequency is.
    public void reWeightSemWeightsByDocSemFreq(TypeStoreGen typeStore){
        for (Map.Entry<String, Integer> entry : typeStore.semFreq.entrySet()) {
            String key = entry.getKey();
            Integer freq = entry.getValue();
            float bonus = (float) (((freq*1.0)/(typeStore.totalSems*1.0))*semFreqBonusMag);
            semValue.replace(key,semValue.get(key)+(bonus));
        }
    }


    /**
     * Adds a rank bonus to question subjects of high complexity.
     * @param phraseComplexity the phrase's complexity
     * @return the bonus weight for that complexity
     */
    public static double addComplexityBonus(int phraseComplexity) {
        if(phraseComplexity>100000 || phraseComplexity == 0){
            return 0;
        }
        if(phraseComplexity<30000){
            return .34;
        }
        return 10000/phraseComplexity;
    }


    //Magic numbers used for function below
    public static double cDiffMag = 1;
    public static double lDiffMag = 9000;
    public static double docBias = 5000;
    public static double spaceMag = 15000;
    /**
     * Formula for choosing whether to use the docRep's name or the graph's name
     * @param ansName name of the answer
     * @param docName name of the bans's docName
     * @param graphName name of the ban's graphName
     * @return true if it is better to use the graph's name
     * TODO: See if the other STR with bans CUI in UMLS DB is the best candidate.
     */
    public static boolean checkPreferGraphName(String ansName, String docName, String graphName) {
        //TODO: consider using regEx instead for faster computation
        if(graphName.indexOf(",")>-1 || graphName.indexOf("[")>-1 || graphName.indexOf("]")>-1 || graphName.indexOf(":")>-1 || graphName.indexOf("(")>-1 || graphName.indexOf(")")>-1 || graphName.indexOf("/")>-1 ){
            return false;
        }

        int ansC = MedGramRanker.calcPhraseComplexity(ansName);
        int docC = MedGramRanker.calcPhraseComplexity(docName);
        int graphC = MedGramRanker.calcPhraseComplexity(graphName);
        int docCDiff = Math.abs(ansC - docC);
        int graphCDiff = Math.abs(graphC - docC);
        int docLDiff = Math.abs(ansName.length() - docName.length());
        int graphLDiff = Math.abs(ansName.length() - graphName.length());

        int numAnsSpaces = ansName.split(" ").length;
        int docSpaceDiff = Math.abs(numAnsSpaces - docName.split(" ").length);
        int graphSpaceDiff = Math.abs(numAnsSpaces - graphName.split(" ").length);
        double docOffSet = (docCDiff*cDiffMag)+(docLDiff*lDiffMag)+(docSpaceDiff*spaceMag)-docBias;
        double graphOffSet = (graphCDiff*cDiffMag)+(graphLDiff*lDiffMag)+(graphSpaceDiff*spaceMag);
        return graphCDiff<docCDiff;
    }



    public static int semTypeCompMag = 100000;
    public static int noComplexityDefault = 50000;
    public static float complexityThresh = 150000; //mininum complexity of a subject of a question to be used.
    public static float bansComplexityThresh = 200000; //minimum complexity of a bad answer to be given
    /**
     * Used to select the best phrase to replace in an answer
     * @param typeWeights semTypeWeight
     * @param complexity the complexity of the phrase
     * @return
     */
    public static float getAnsPhraseScore(float typeWeights, int complexity) {
        if(complexity>complexityThresh){
           return 0;
        }else if(complexity == 0){
            return typeWeights*(noComplexityDefault);
        }
        return (typeWeights*(semTypeCompMag))+(complexityThresh-complexity);
    }

    //Magic Weights used to give bonuses to questions for having answers at a good tier.
    public static double[] reqsPureGraph ={0.2,.03,0,0,0,0,0,0};
    public static double[] reqsParentAssist = {1,.75,.5,.3,0,0,0};
    public static double[] reqsDocConfirm = {.4,.2,.1,.05,0,0,0,0};


    //General weights for ranking the quality of the Question

        //By the semtype
        public static double semTypeMag = 1.0;
     //   public static double semTypeBonus(MedicalQuestion q){
//            return (q.replacementPhraseScore-1)*semTypeMag;
//        }

        //By their Q/A lengths
        public static double noAnswerPhraseTree = -.8;
        public static double ShortAnswer = .2;
        public static double TadLongAnswer = -.1;
        public static double LongAnswer = -.5;
        public static double VeryLongAnswer = -.8;
        public static double QTooShort = -.8;
        public static double QKindaShort = -.3;
        public static double QGoodLength = .1;

        //Length threshes
        public static int shortALen = 6;
        public static int tadLongALen = 9;
        public static int longALen = 12;
        public static int shortQLen = 4;
        public static int kindaShortQLen = 6;


}
