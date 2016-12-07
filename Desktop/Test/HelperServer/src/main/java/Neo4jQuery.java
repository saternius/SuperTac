import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.Collections;
//TODO: Keep track of the relationships generated and make bad answers a class with history of generation.

/**
 * Created by davidsilin on 5/17/16.
 */
public class Neo4jQuery {
    private Driver driver;
    private Session session;
    private static final int LIMIT = 50; //maximum number of candidates to get from a query
    //Queries for finding candidates using only the graph and answer cui
    private final static String SIB_RBRN_PURE = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e)-[:RN]-(b:TUI), (a)-[:SIB]-(b) RETURN DISTINCT b LIMIT "+LIMIT+";";
    private final static String SIB_PURE = "MATCH (a:TUI{cid:\"CUID\"})-[:SIB]-(b:TUI),(a)-[:PAR]-(e)-[:CHD]-(b) RETURN DISTINCT b LIMIT "+LIMIT+";";
    private final static String PARCHD_RBRN_PURE = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e)-[:RN]-(b:TUI), (a)-[:PAR]-(e)-[:CHD]-(b), (a)-[:RO]-(b) RETURN DISTINCT b LIMIT "+LIMIT+";";
    private final static String PARCHD_PURE = "MATCH (a:TUI{cid:\"CUID\"})-[:PAR]-(e)-[:CHD]-(b:TUI), (a)-[:RO]-(b) RETURN DISTINCT b LIMIT "+LIMIT+";";
    private final static String RBRN_PURE = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e)-[:RN]-(b:TUI), (a)-[:RO]-(b) RETURN DISTINCT b LIMIT "+LIMIT+";";

    //Queries used for determining the tier of a replacement cui in relation to the answer cui
    private final static String SIB_RBRN_SEARCH = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e)-[:RN]-(b:TUI{cid:\"CUID2\"}), (a)-[:SIB]-(b) RETURN a,e,b LIMIT "+LIMIT+";";
    private final static String SIB_SEARCH = "MATCH (a:TUI{cid:\"CUID\"})-[:SIB]-(b:TUI{cid:\"CUID2\"}), (a)-[:PAR]-(e)-[:CHD]-(b) RETURN a,e,b LIMIT "+LIMIT+";";
    private final static String PARCHD_RBRN_SEARCH = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e)-[:RN]-(b:TUI{cid:\"CUID2\"}), (a)-[:PAR]-(e)-[:CHD]-(b), (a)-[:RO]-(b) RETURN a,e,b LIMIT "+LIMIT+";";
    private final static String PARCHD_SEARCH = "MATCH (a:TUI{cid:\"CUID\"})-[:PAR]-(e)-[:CHD]-(b:TUI{cid:\"CUID2\"}), (a)-[:RO]-(b) RETURN a,e,b LIMIT "+LIMIT+";";
    private final static String RBRN_SEARCH = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e)-[:RN]-(b:TUI{cid:\"CUID2\"}), (a)-[:RO]-(b) RETURN a,e,b LIMIT "+LIMIT+";";

    //Queries to find candidates with a certain parent and answer cui
    private final static String SIB_RBRN_PARENT = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e:PTUI{cid:\"PCUID\"})-[:RN]-(b:TUI), (a)-[:SIB]-(b) RETURN a,e,b LIMIT "+LIMIT+";";
    private final static String PARCHD_RBRN_PARENT = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e:PTUI{cid:\"PCUID\"})-[:RN]-(b:TUI), (a:TUI{cid:\"CUID\"})-[:PAR]-(e:PTUI{cid:\"PCUID\"})-[:CHD]-(b:TUI), (a)-[:RO]-(b) RETURN a,e,b LIMIT "+LIMIT+";";
    private final static String PARCHD_PARENT = "MATCH (a:TUI{cid:\"CUID\"})-[:PAR]-(e:PTUI{cid:\"PCUID\"})-[:CHD]-(b:TUI), (a)-[:RO]-(b) RETURN a,e,b LIMIT "+LIMIT+";";
    private final static String RBRN_PARENT = "MATCH (a:TUI{cid:\"CUID\"})-[:RB]-(e:PTUI{cid:\"PCUID\"})-[:RN]-(b:TUI), (a)-[:RO]-(b) RETURN a,e,b LIMIT "+LIMIT+";";

    //Queries to figure out if replacement cui is a synonym/subSet/superSet of an answer cui
    private final static String SYN_Q ="MATCH (a:TUI{cid:\"CUID\"})-[:SY|RQ]-(b:TUI{cid:\"CUID2\"}) RETURN a,b";
    private final static String SUPER_Q ="MATCH (a:TUI{cid:\"CUID\"})-[:PAR|RB]-(b:TUI{cid:\"CUID2\"}) RETURN a,b";
    private final static String SUB_Q ="MATCH (a:TUI{cid:\"CUID\"})-[:CHD|RN]-(b:TUI{cid:\"CUID2\"}) RETURN a,b";

    public static String[] pureReq = {SIB_RBRN_PURE,SIB_PURE,PARCHD_RBRN_PURE,PARCHD_PURE,RBRN_PURE};
    private static String[] reqs_titles = {"SIB_RBRN","SIB","PARCHD_RBRN","PARCHD","RBRN"};

    public static String[] reqs_parent_titles = {"SIB_RBRN","PARCHD_RBRN","PARCHD","RBRN"};
    private static String[] reqs_search = {SIB_RBRN_SEARCH,SIB_SEARCH,PARCHD_RBRN_SEARCH,PARCHD_SEARCH,RBRN_SEARCH};
    private static String[] reqs_parent = {SIB_RBRN_PARENT,PARCHD_RBRN_PARENT,PARCHD_PARENT,RBRN_PARENT};

    private boolean printQueries = false;

    public Neo4jQuery(String ip){
        try {
            driver = GraphDatabase.driver("bolt://" + ip, AuthTokens.basic("neo4j", "popcorn"));
            session = driver.session();
            System.out.println("neo4j init [" + ip + "]");
        }catch (Exception e){
            System.out.println("NEO4J IS NOT RUNNING..");
        }
    }


    /**
     *  returns numNeeded bad answers from an answer CUI
     *  Uses only the neo4j graph to get candidates.
     * @param ansCUI
     * @param semType
     * @param ansC          answer's complexity
     * @param limit         maximum tier to search at
     * @param start         starting tier to search at
     * @param usedCUIs
     * @return
     */
    public ArrayList<BadAns> QueryGraph(String ansCUI, String semType, int ansC,int limit,int start, ArrayList<String> usedCUIs, int maxGen){
        String TUI = SemTUI.getTUI(semType);
        if(TUI==null){
            return null;
        }
        return Query(TUI, ansCUI, usedCUIs, ansC,limit,start, maxGen);
    }


    /**
     * Helper of QueryGraph
     * @param TUI
     * @param CUID
     * @param cuids
     * @param ansC
     * @param limit
     * @param start
     * @return
     */
    ArrayList<BadAns> Query(String TUI,String CUID, ArrayList<String> cuids, int ansC, int limit, int start, int maxGen) {
        ArrayList<BadAns> BadAnswers = new ArrayList<>();

        int tierLimit = Math.min(pureReq.length,limit);
        for(int i=start; i<tierLimit;i++){
            ArrayList<BadAns> candidates  = new ArrayList<>();
            String queryString = pureReq[i].replaceAll("TUI",TUI).replaceAll("CUID",CUID);
            if(printQueries){
                System.out.println(queryString);
            }
            StatementResult result = run(queryString);
            while ( result!=null && result.hasNext() ) {
                Record record = result.next();

                String cuid_tmp = record.get(0).asNode().get("cid").toString();
                cuid_tmp = cuid_tmp.substring(1, cuid_tmp.length()-1);

                String str_tmp = record.get(0).asNode().get("str").toString();
                str_tmp = str_tmp.substring(1, str_tmp.length()-1);

                if(str_tmp.contains(",") && str_tmp.split(" ").length ==2){
                    String[] tmp = str_tmp.split(",");
                    str_tmp = tmp[1].substring(1, tmp[1].length()) + " " + tmp[0];
                }

                boolean isSynonymous = checkSynonymous(CUID,cuid_tmp,TUI);
                boolean isSuperSet = checkSuperSet(CUID,cuid_tmp,TUI);
                boolean isSubSet = checkSubSet(CUID,cuid_tmp,TUI);
                String sup = isSuperSet?", SUPERSET":"";
                String sub = isSubSet?", SUBSET":"";
                String notation = sup+sub;
                int candComp = MedGramRanker.calcPhraseComplexity(str_tmp);
                if(candComp> MedicalWeights.bansComplexityThresh){

                }else if(isSynonymous){

               // }else if(isSuperSet || isSubSet) {
               //     sm.notes.makeTally("broad/narrow removed");
                }else if(!cuid_tmp.equals(CUID) && !cuids.contains(cuid_tmp) && str_tmp.split(" ").length <= 3 && !str_tmp.contains("[") && !str_tmp.contains("(")&& !str_tmp.contains(",")&& !str_tmp.contains("-")){
                        BadAns candidate  = new BadAns();
                        candidate.subject = str_tmp;
                        candidate.CUI = cuid_tmp;
                        candidate.origin = "GenericGraph, "+reqs_titles[i]+" "+notation;
                        candidate.bonusRank+= MedicalWeights.reqsPureGraph[i];
                        candidate.phraseComplexity = candComp;
                        candidate.neoQuery = queryString;
                        candidates.add(candidate);
                        cuids.add(cuid_tmp);
                }
            }

            Collections.sort(candidates, (b1, b2) -> {
                return Math.abs(ansC-b1.phraseComplexity) - Math.abs(ansC-b2.phraseComplexity); // Ascending
            });

            BadAnswers.addAll(candidates);
            if(BadAnswers.size()>=maxGen){
                return BadAnswers;
            }
        }
        return BadAnswers;
    }


    public boolean checkSynonymous(String CUID, String CUID2, String TUI){
        String queryString = SYN_Q.replaceAll("TUI",TUI).replaceAll("CUID2",CUID2).replaceAll("CUID",CUID);
        StatementResult result = run(queryString);
        while ( result!=null && result.hasNext() ) {
            return true;
        }
        return false;
    }

    public boolean checkSuperSet(String CUID, String CUID2, String TUI){
        String queryString = SUPER_Q.replaceAll("TUI",TUI).replaceAll("CUID2",CUID2).replaceAll("CUID",CUID);
        StatementResult result = run(queryString);
        while ( result!=null && result.hasNext() ) {
            return true;
        }
        return false;
    }

    public boolean checkSubSet(String CUID, String CUID2, String TUI){
        String queryString = SUB_Q.replaceAll("TUI",TUI).replaceAll("CUID2",CUID2).replaceAll("CUID",CUID);
        StatementResult result = run(queryString);
        while ( result!=null && result.hasNext() ) {
            return true;
        }
        return false;
    }




    /**
     * Returns the best tier the replacement CUI satisfies
     * @param ansCUI
     * @param replaceCUI
     * @param TUI
     * @param answerData
     * @return
     */
    public Neo4jRetStruct getConceptTier(String ansCUI, String replaceCUI, String TUI, TypeStoreData answerData) {
        for(int i=0; i<reqs_search.length;i++) {
            String queryString = reqs_search[i].replaceAll("TUI", TUI).replaceAll("CUID2", replaceCUI).replaceAll("CUID",ansCUI);
            if(printQueries){
                System.out.println(queryString);
            }
            StatementResult result = run(queryString);
            while ( result != null && result.hasNext() ) {
                Record record = result.next();
                String cuid_tmp = record.get(1).asNode().get("cid").toString();
                cuid_tmp = cuid_tmp.substring(1, cuid_tmp.length()-1);

                String str_tmp = record.get(1).asNode().get("str").toString();
                String tui_tmp = record.get(1).asNode().labels().toString().substring(1,5);


                str_tmp = str_tmp.substring(1, str_tmp.length()-1);
                if(str_tmp.contains(",") && str_tmp.split(" ").length ==2){
                    String[] tmp = str_tmp.split(",");
                    str_tmp = tmp[1].substring(1, tmp[1].length()) + " " + tmp[0];
                }
                //System.out.println(cuid_tmp+":"+str_tmp);

                String ansGraphName = record.get(0).asNode().get("str").toString();
                ansGraphName = ansGraphName.substring(1,ansGraphName.length()-1);
                String repGraphName = record.get(2).asNode().get("str").toString();
                repGraphName = repGraphName.substring(1,repGraphName.length()-1);
                Neo4jRetStruct retStruct = new Neo4jRetStruct();

                boolean isSynonymous = checkSynonymous(ansCUI,replaceCUI,TUI);
                boolean isSuperSet = checkSuperSet(ansCUI,replaceCUI,TUI);
                boolean isSubSet = checkSubSet(ansCUI,replaceCUI,TUI);
                String sup = isSuperSet?", SUPERSET":"";
                String sub = isSubSet?", SUBSET":"";
                String notation = sup+sub;

                int candComp = MedGramRanker.calcPhraseComplexity(str_tmp);
                int tier = i;
                if(candComp> MedicalWeights.bansComplexityThresh || isSynonymous /* || isSuperSet || isSubSet*/ ||ansCUI.equals(replaceCUI)){
                    tier=5;
                }
                if(str_tmp.split(" ").length <= 3 && !str_tmp.contains("[") && !str_tmp.contains("(")&& !str_tmp.contains(",")&& !str_tmp.contains("-")) {
                    //badAnswers[counter].text = str_tmp;
                    //makeTally(curThread,"ReqDocTier["+i+"]");
                    retStruct.ansCUI = ansCUI;
                    retStruct.ansGraphName = ansGraphName;
                    retStruct.repGraphName = repGraphName;
                    retStruct.repCUI = replaceCUI;
                    retStruct.parentCUI = cuid_tmp;
                    retStruct.parentStr = str_tmp;
                    retStruct.tier = tier;
                    retStruct.answerData = answerData;
                    retStruct.parentTUI = tui_tmp;
                    retStruct.bonusRank = MedicalWeights.reqsDocConfirm[i];
                    retStruct.queryString = queryString;
                    retStruct.notation = notation;
                    return retStruct;
                }
            }


        }
        return null;
    }



    /**
     * Returns badAnswers that has a good relationship with the parentCUI and ansCUI
     * @param ansCUI
     * @param pCUI
     * @param parentTUI
     * @param TUI
     * @param usedCUIS
     * @param left
     * @param tier
     * @param subC
     * @return
     */
    public ArrayList<BadAns> getCandidatesWithParent(String ansCUI, String pCUI, String parentTUI, String TUI, ArrayList<String> usedCUIS,int left,int tier, int subC) {
        ArrayList<BadAns> BadAnswers = new ArrayList<BadAns>();
        for(int i=0; i<reqs_parent.length;i++) {
            ArrayList<BadAns> candidates = new ArrayList<>();
            String queryString = reqs_parent[i].replaceAll("PTUI",parentTUI).replaceAll("TUI", TUI).replaceAll("PCUID", pCUI).replaceAll("CUID", ansCUI);
            if(printQueries){
                System.out.println(queryString);
            }
            StatementResult result = run(queryString);
            while (result!=null && result.hasNext()) {
                Record record = result.next();
                String cuid_tmp = record.get(2).asNode().get("cid").toString();
                cuid_tmp = cuid_tmp.substring(1, cuid_tmp.length()-1);
                String str_tmp = record.get(2).asNode().get("str").toString();
                str_tmp = str_tmp.substring(1, str_tmp.length()-1);

                if(str_tmp.contains(",") && str_tmp.split(" ").length ==2){
                    String[] tmp = str_tmp.split(",");
                    str_tmp = tmp[1].substring(1, tmp[1].length()) + " " + tmp[0];
                }

                String ansGraphName = record.get(0).asNode().get("str").toString();
                ansGraphName = ansGraphName.substring(1,ansGraphName.length()-1);
                String parGraphName = record.get(0).asNode().get("str").toString();
                parGraphName = parGraphName.substring(1,parGraphName.length()-1);


                boolean isSynonymous = checkSynonymous(ansCUI,cuid_tmp,TUI);
                boolean isSuperSet = checkSuperSet(ansCUI,cuid_tmp,TUI);
                boolean isSubSet = checkSubSet(ansCUI,cuid_tmp,TUI);
                String sup = isSuperSet?", SUPERSET":"";
                String sub = isSubSet?", SUBSET":"";
                String notation = sup+sub;

                int candComp = MedGramRanker.calcPhraseComplexity(str_tmp);
                if(candComp> MedicalWeights.bansComplexityThresh) {
                    //makeTally(curThread,"bans to simple");
                }else if(isSynonymous /* || isSuperSet || isSubSet*/){
                    //makeTally(curThread,"parent removed due to syn/broad");
                }
                else if(!cuid_tmp.equals(ansCUI) && !usedCUIS.contains(cuid_tmp) && str_tmp.split(" ").length <= 3 && !str_tmp.contains("[") && !str_tmp.contains("(")&& !str_tmp.contains(",")&& !str_tmp.contains("-")) {
                    String origin = "PARENTGEN, TIER["+tier+"]: " + reqs_parent_titles[i]+ " ("+ansGraphName+")-("+parGraphName+")-("+str_tmp+")"+notation;
                    candidates.add(new BadAns(str_tmp, cuid_tmp, origin, pCUI, MedicalWeights.reqsDocConfirm[tier]* MedicalWeights.reqsParentAssist[i],queryString));
                    usedCUIS.add(cuid_tmp);
                }
            }

            Collections.sort(candidates, (b1, b2) -> {
                return Math.abs(subC-b1.phraseComplexity) - Math.abs(subC-b2.phraseComplexity); // Ascending
            });


//            for(int j=0; j<Math.min(candidates.size(),left);j++){
//                makeTally(curThread,"Parent["+tier+"]["+reqs_parent_titles[i]+"]");
//            }

            BadAnswers.addAll(candidates);
            left-=candidates.size();
            if(left<=0){
                return BadAnswers;
            }

        }

        return BadAnswers;
    }

//    private void makeTally(ThreadNBloc curThread, String s) {
//        try {
//            curThread.sm.notes.makeTally(s);
//        }catch (Exception e ){};
//    }

    StatementResult run(String query){
        return session.run(query);
    }
    /**
     * closes the server connection gracefully
     */
    public void closeStuff(){
        if(session!=null) {
            session.close();
        }
        if(driver!=null) {
            driver.close();
        }
        driver = null;
        session = null;
    }
}
