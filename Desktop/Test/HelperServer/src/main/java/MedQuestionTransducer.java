
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.*;

/**
 * Created by davidsilin on 4/12/16.
 */
public class MedQuestionTransducer implements Serializable{
    static int docRepTierLimit = 4;     //Lowest tier acceptable for docRep
    static int maxBans = 6;             //Num Bans to generate *Keep at 3 for now*
    static int minBans = 3;             //Num Bans needed to be considered a full question

    static HashMap bansMap = new HashMap<String,ArrayList<BadAns>>();
//    static HashMap<String,AnswerData> ansMap = new HashMap<String,AnswerData>();

//    public void generateQuestionsFromParse(Tree inputTree){
//        System.out.println("WHY AM I A THINK??");
//        MedicalQuestion q = new MedicalQuestion();
//        q.setSourceTree(inputTree);
//        q.setIntermediateTree(inputTree.deeperCopy());
//        generateQuestionsFromParse(q);
//    }

//    public static void removeDuplicateMedQuestions(Collection<MedicalQuestion> givenQuestions) {
//        java.util.Map<String, MedicalQuestion> yieldMap = new HashMap<String, MedicalQuestion>();
//        String yield;
//        //add questions that used NP Clarification first
//        for(MedicalQuestion q: givenQuestions){
//            if(q.getFeatureValue("performedNPClarification") == 0.0){
//                continue;
//            }
//            yield = q.getTree().yield().toString();
//            if(yieldMap.containsKey(yield)){
//                if(GlobalProperties.getDebug()) System.err.println("Removing duplicate: "+yield);
//                continue;
//            }
//            yieldMap.put(yield, q);
//        }
//        //now add any new questions that don't involve NP Clarification
//        for(MedicalQuestion q: givenQuestions){
//            if(q.getFeatureValue("performedNPClarification") == 1.0){
//                continue;
//            }
//            yield = q.getTree().yield().toString();
//            if(yieldMap.containsKey(yield)){
//                if(GlobalProperties.getDebug()) System.err.println("Removing duplicate: "+yield);
//
//                //if a previous question that involved NP Clarification has the same yield (i.e., text),
//                //then mark it as using NP Clarification for the answer only
//                MedicalQuestion other = yieldMap.get(yield);
//                if(other.getFeatureValue("performedNPClarification") == 1.0 && other.getSourceSentenceNumber() == q.getSourceSentenceNumber()){
//                    //other.setFeatureValue("performedNPClarificationAnswerOnly", 1.0);
//                    other.setFeatureValue("performedNPClarification", 0.0);
//                }
//                continue;
//            }
//
//            yieldMap.put(yield, q);
//        }
//
//        givenQuestions.clear();
//        givenQuestions.addAll(yieldMap.values());
//    }

//    public static MedicalQuestion makeMedical(Question quest, HashMap<String,AnswerData> ansMap, SessionManager sm, MetaMapWrapper mmw){
//        MedicalQuestion medQ = new MedicalQuestion(quest);
//
//        //Get AnswerPhrase
//        if(medQ.getAnswerPhraseTree() == null){
//            sm.notes.makeTally("No Ans");
//            return null;
//        }
//
//        String answer = AnalysisUtilities.getCleanedUpYield(medQ.getAnswerPhraseTree());
//        medQ.correctPhrase = answer.toLowerCase();
//
////        TODO: fix the numSemHandler to fit the parallel model
////        if(NumSemTypeHandler.containsNum(answer,mmw,sm)){
////            NumSemTypeHandler.handleNumSubject(answer,typeStore.typeStore,medQ, sm.MW, parserURL);
////            return medQ;
////        }
//        String BestCUID;
//        int ansC;
//        String topPhrase;
//        String answerText;
//        List<String> answerMatches;
//        //Pass the answerPhrase through metamap and select the best replacement Subject.
//        if(ansMap.containsKey(answer)){
//            AnswerData ansData = ansMap.get(answer);
//            if(ansData == null || ansData.ignore || ansData.ansC>MedicalWeights.complexityThresh){
//                sm.notes.makeTally("No Complex Subject");
//                return null;
//            }
//            BestCUID = ansData.ansCUI;
//            ansC = ansData.ansC;
//            topPhrase = ansData.topPhrase;
//            answerText = ansData.ansText;
//            answerMatches = ansData.matches;
//            medQ.ansMatches = answerMatches;
//            medQ.topPhrase = topPhrase;
//            medQ.utterance = answerText;
//            medQ.subject = ansData.ansSubject;
//            medQ.replacementPhraseScore = ansData.replacementScore;
//            medQ.replacementSemType = ansData.repSemType;
//            medQ.ansCUI = BestCUID;
//            medQ.setPhraseComplexity(ansC);
//        }else{
//            try {
//                List<Result> resultList = mmw.parseString(answer);
//                Utterance utterance = resultList.get(0).getUtteranceList().get(0);
//                QuestionMedicalSemantics QM = new QuestionMedicalSemantics(utterance);
//                BestCUID = QM.chooseBestPhraseAndGetCUID(sm);
//                if (QM.ignore) {
//                    sm.notes.makeTally("No Complex Subject");
//                    ansMap.put(answer,null);
//                    return null;
//                }
//                topPhrase = QM.getTopPhraseText();
//                medQ.replacementPhraseScore = QM.top.semanticScore;
//                medQ.replacementSemType = QM.top.topSemType;
//
//                //Give bonuses for subject complexity
//                medQ.subject = QM.top.subject;
//                ansC = MedGramRanker.calcPhraseComplexity(medQ.subject);
//                answerText = QM.AnswerText;
//                answerMatches = QM.getTopPhraseMatchedWords();
//                AnswerData ansData = new AnswerData(topPhrase,QM.AnswerText,QM.getTopPhraseMatchedWords(),ansC,BestCUID,QM.top.subject,QM.top.semanticScore,QM.top.topSemType,false);
//                ansMap.put(answer,ansData);
//                medQ.ansCUI = BestCUID;
//                medQ.setPhraseComplexity(ansC);
//                medQ.utterance = answerText;
//                medQ.topPhrase = topPhrase;
//                medQ.ansMatches = answerMatches;
//                if(ansC>MedicalWeights.complexityThresh){
//                    sm.notes.makeTally("No Complex Subject");
//                    ansMap.put(answer,null);
//                    return null;
//                }
//            } catch (Exception e) {
//                sm.notes.makeTally("Malfunctioned");
//                return null;
//            }
//        }
//        return medQ;
//    }

    public static String selectBans(JSONObject qData, Neo4jQuery neo_q) throws Exception{
        ArrayList<TypeStoreData> answers = new ArrayList<>();
        JSONArray jAns = qData.getJSONArray("answers");
        for(int i=0; i<jAns.length();i++){
            JSONObject ans = jAns.getJSONObject(i);
            answers.add(new TypeStoreData(ans.getString("text"),ans.getString("CUID")));
        }
        int subC = qData.getInt("subC");
        String semType = qData.getString("ansSemType");
        String subject = qData.getString("subject");
        ArrayList<BadAns>bansArray = shortCascade(qData.getString("ansCUI"),answers, neo_q, subC,semType,subject);
        if(bansArray == null || bansArray.size()<3){
            if(bansArray !=null) {
                //sm.notes.makeTally("Insufficient Num Ans");
            }
            return "{\"success\":\"false\", \"reason\":\"insufficient answers\"}";
        }

        ArrayList<BadAns> smallerBans = new ArrayList<>();
        for(int i=0; i<Math.min(bansArray.size(),maxBans);i++){
            smallerBans.add(bansArray.get(i));
        }
        bansArray = smallerBans;
        return "{\"success\":\"true\",\"bData\":"+toJSONString(bansArray)+"}";
    }

    public static String toJSONString(ArrayList<BadAns> bansArray){
        String ret = "[";
        for(BadAns bans : bansArray){
            ret+=bans.toLightJSON()+",";
        }
        ret = ret.substring(0,ret.length()-1);
        return ret+"]";
    }

//    /**
//     * Converts question to medQuestions, and generates bad answers for replacement subject
//     * @param quest
//     * @param typeStore
//     * @param server_id
//     * @return
//     */
//    public static MedicalQuestion generateBadAnswersFromParse(Question quest, TypeStoreGen typeStore, int server_id, SessionManager sm, ThreadNBloc curThread){
//        //Get appropriate server
//        MetaMapWrapper mmw = Servers.getMMW(server_id);
//        Neo4jQuery neo_q = Servers.getNeo4jQuery(sm.userID,server_id);
//        MedicalQuestion medQ = new MedicalQuestion(quest);
//        String parserURL = "http://"+Servers.getIp(server_id)+":4567/parseS?s=";
//
//        //Step 5.1: Get the best replacement subject
//
//            //Get AnswerPhrase
//            if(medQ.getAnswerPhraseTree() == null){
//                sm.notes.makeTally("No Ans");
//                medQ.ignore();
//                return medQ;
//            }
//            String answer = AnalysisUtilities.getCleanedUpYield(medQ.getAnswerPhraseTree());
//            medQ.correctPhrase = answer.toLowerCase();
//            if(NumSemTypeHandler.containsNum(answer,mmw,sm)){
//                NumSemTypeHandler.handleNumSubject(answer,typeStore.typeStore,medQ, sm.MW);
//                return medQ;
//            }
//
//            String BestCUID;
//            int ansC;
//            String topPhrase;
//            String answerText;
//            List<String> answerMatches;
//            //Pass the answerPhrase through metamap and select the best replacement Subject.
//            if(ansMap.containsKey(answer)){
//                AnswerData ansData = ansMap.get(answer);
//                if(ansData.ignore || ansData.ansC>MedicalWeights.complexityThresh){
//                    sm.notes.makeTally("No Complex Subject");
//                    medQ.ignoredFor = "No Complex Subject";
//                    return medQ;
//                }
//                BestCUID = ansData.ansCUI;
//                ansC = ansData.ansC;
//                topPhrase = ansData.topPhrase;
//                answerText = ansData.ansText;
//                answerMatches = ansData.matches;
//                medQ.subject = ansData.ansSubject;
//                medQ.replacementPhraseScore = ansData.replacementScore;
//                medQ.replacementSemType = ansData.repSemType;
//                medQ.ansCUI = BestCUID;
//                medQ.setPhraseComplexity(ansC);
//            }else{
//                try {
//                    List<Result> resultList = mmw.parseString(answer);
//                    Utterance utterance = resultList.get(0).getUtteranceList().get(0);
//                    QuestionMedicalSemantics QM = new QuestionMedicalSemantics(utterance);
//                    BestCUID = QM.chooseBestPhraseAndGetCUID(sm);
//                    if (QM.ignore) {
//                        sm.notes.makeTally("No Complex Subject");
//                        medQ.ignoredFor = "No Complex Subject";
//                        ansMap.put(answer,new AnswerData());
//                        return medQ;
//                    }
//                    topPhrase = QM.getTopPhraseText();
//                    medQ.replacementPhraseScore = QM.top.semanticScore;
//                    medQ.replacementSemType = QM.top.topSemType;
//
//                    //Give bonuses for subject complexity
//                    medQ.subject = QM.top.subject;
//                    ansC = MedGramRanker.calcPhraseComplexity(medQ.subject);
//                    answerText = QM.AnswerText;
//                    answerMatches = QM.getTopPhraseMatchedWords();
//                    AnswerData ansData = new AnswerData(topPhrase,QM.AnswerText,QM.getTopPhraseMatchedWords(),ansC,BestCUID,QM.top.subject,QM.top.semanticScore,QM.top.topSemType,false);
//                    ansMap.put(answer,ansData);
//                    medQ.ansCUI = BestCUID;
//                    medQ.setPhraseComplexity(ansC);
//
//                    if(ansC>MedicalWeights.complexityThresh){
//                        sm.notes.makeTally("No Complex Subject");
//                        medQ.ignoredFor = "No Complex Subject";
//                        ansMap.put(answer,new AnswerData());
//                        return medQ;
//                    }
//                } catch (Exception e) {
//                    medQ.ignore();
//                    sm.notes.makeTally("Malfunctioned");
//                    return medQ;
//                }
//            }
//
//
//            //Step 5.2: gen an array of BANS
//            ArrayList<BadAns>bansArray = cascadingTierBansGen(typeStore,BestCUID,neo_q,medQ,medQ.subject,ansC,curThread,sm);
//            if(bansArray == null || bansArray.size()<3){
//                if(bansArray !=null) {
//                    sm.notes.makeTally("Insufficient Num Ans");
//                }
//                medQ.ignore();
//                return medQ;
//            }
//            ArrayList<BadAns> smallerBans = new ArrayList<>();
//            for(int i=0; i<Math.min(bansArray.size(),maxBans);i++){
//                smallerBans.add(bansArray.get(i));
//            }
//            bansArray = smallerBans;
//
//            //5.3 Store other meta information and clean subject/bans text.
//            giveBansScores(bansArray,medQ);
//            try{
//                cleanQuestion(answerText,topPhrase,answerMatches,bansArray,medQ, parserURL);
//            } catch (Exception e){
//                System.out.println("python exception<br>");
//                System.out.println(e.toString());
//                sm.notes.makeTally("Python Failure");
//                medQ.ignore();
//            }
//            return medQ;
//    }

//    public static void giveBansScores(ArrayList<BadAns> bansArray, MedicalQuestion medQ){
//        medQ.bansArray = bansArray;
//        //I converted the bans ArrayList for convenience of keeping compatibility with previous code
//        //badAnswers will be replaced with bansArray entirely in the near future.
//        medQ.editScore(bansArray.get(0).bonusRank,"BA1Bonus");
//        medQ.editScore(bansArray.get(1).bonusRank,"BA2Bonus");
//        medQ.editScore(bansArray.get(2).bonusRank,"BA3Bonus");
//    }

    /**
     * Fetches n number of fake answers purely from the neo4j graph (No document contexts)
     * @param BestCUID  answer's CUI
     * @param neo_q     neo4j server
     * @param subC      subject complexity
     * @param limit     maximum tier to search at
     * @param start     which tier to start at
     * @param usedCUIDS cuis to ignore
     * @return
     */
    private static ArrayList<BadAns> fillVoidWithGraph(String BestCUID, Neo4jQuery neo_q,  int subC, int limit, int start, ArrayList<String> usedCUIDS, String semType) {
        return neo_q.QueryGraph(BestCUID,semType,subC,limit,start,usedCUIDS, maxBans);
    }

    public static ArrayList<BadAns> shortCascade(String BestCUID, ArrayList<TypeStoreData> answers, Neo4jQuery neo_q, int subC, String semType, String subject){
        ArrayList<String> usedCUIDS = new ArrayList<>();
        usedCUIDS.add(BestCUID);
        if(answers == null){
            return pureGraphSearch(BestCUID,neo_q, subC,usedCUIDS, semType);
        }

        ArrayList<BadAns> BadAnswers = new ArrayList<>();
        answers = removeSameCUID(answers, BestCUID);
        String TUI = SemTUI.getTUI(semType);
        List<List<Neo4jRetStruct>> bansTierList = genBansTierList(answers, usedCUIDS, BestCUID, TUI, neo_q);

        //Iterate through the list of tiers
        for(int i=0; i<docRepTierLimit;i++){
            //ShoeHorned graphPure(0) > docRep(3) TODO: create and array of official tier list [ combining both docRep and pureG ]
            if(i==3){
                addBansWithTopPureGraph(BadAnswers,BestCUID, neo_q, subC, usedCUIDS, semType);
                if(BadAnswers.size()>= maxBans){ break;}
            }

            List<Neo4jRetStruct> tierCands = bansTierList.get(i);   //all the docRep candidates
            ArrayList<String[]> parentRefs = new ArrayList<>();     //list of parentRefs to be used for intermediate searches
            ArrayList<BadAns> finalCands = new ArrayList<>();       //candidates of this tier that passes all threshes

            for(int j=0; j<tierCands.size();j++){
                Neo4jRetStruct retStruct = tierCands.get(j);
                String[] parentDetails = new String[2];
                parentDetails[0] = retStruct.parentCUI;
                parentDetails[1] = retStruct.parentTUI;
                String origin = "DOCREP, TIER: "+i+"   ("+retStruct.ansGraphName+")-("+retStruct.parentStr+")-("+retStruct.repGraphName+")"+retStruct.notation;
                String path = "Parent: "+retStruct.parentCUI;
                BadAns bans = new BadAns(retStruct.answerData.text,retStruct.repCUI,origin,path,retStruct.bonusRank, retStruct.queryString);
                bans.graphName = retStruct.repGraphName;
                bans.ansName = subject;
                bans.parentDetails = parentDetails;
                if(bans.phraseComplexity < MedicalWeights.bansComplexityThresh) {
                    finalCands.add(bans);
                    usedCUIDS.add(retStruct.repCUI);
                }
            }

            sort(finalCands,subC);

            int k=0;
            while( BadAnswers.size() < maxBans && k<finalCands.size() ){
                addBans(BadAnswers,finalCands.get(k));
                parentRefs.add(finalCands.get(k).parentDetails);
                k++;
            }

            if(BadAnswers.size()>= maxBans){ break;}

            //Using the parent of the answers on this tier to generate other candidates that should be within this tier
            for(int j=0; j<parentRefs.size(); j++){
                String pCUI = parentRefs.get(j)[0];
                String pTUI = parentRefs.get(j)[1];
                ArrayList<BadAns> bans = neo_q.getCandidatesWithParent(BestCUID,pCUI,pTUI,TUI,usedCUIDS, (maxBans - BadAnswers.size()),i,subC);
                addAllBans(BadAnswers,bans);
                if(BadAnswers.size() >= maxBans){ break; }
            }
            if(BadAnswers.size() >= maxBans){ break; }
        }

        //default pureGraph search
        if(BadAnswers.size()< maxBans){
            ArrayList<BadAns> bans = fillVoidWithGraph(BestCUID, neo_q, subC,100,1,usedCUIDS, semType);
            for (int i = 0; i < bans.size(); i++) {
                addBans(BadAnswers,bans.get(i));
            }
        }


        if(BadAnswers.size()>=minBans){
            bansMap.put(BestCUID,BadAnswers);
        }else{
            bansMap.put(BestCUID,null);
        }

        return BadAnswers;
    }

    private static void sort(ArrayList<BadAns> finalCands, int subC) {
        try {
            Collections.sort(finalCands, Comparator.comparingInt(b -> Math.abs(subC - b.phraseComplexity)));
        }catch(Exception e){
            System.out.println("Failed to sort");
        }
    }

    private static void addBansWithTopPureGraph(ArrayList<BadAns> BadAnswers, String BestCUID, Neo4jQuery neo_q, int subC, ArrayList<String> usedCUIDS, String semType) {
        ArrayList<BadAns> bans = fillVoidWithGraph(BestCUID, neo_q, subC,1,0,usedCUIDS, semType);
        if(bans!=null) {
            for (int j = 0; j < bans.size(); j++) {
                addBans(BadAnswers,bans.get(j));
            }
        }
    }

    /**
     * generates a list of answers based off of their 'tier'
     * @param answers
     * @param usedCUIDS
     * @param BestCUID
     * @param TUI
     * @param neo_q
     * @return
     */
    private static List<List<Neo4jRetStruct>> genBansTierList(ArrayList<TypeStoreData> answers, ArrayList<String> usedCUIDS, String BestCUID, String TUI, Neo4jQuery neo_q) {
        List<List<Neo4jRetStruct>> bansTierList = new ArrayList<>();
        for(int i=0; i<=5;i++){
            bansTierList.add(new ArrayList<>());
        }

        for(int i=0; i<answers.size();i++){
            String replaceCUID = answers.get(i).CUID;
            if(usedCUIDS.contains(replaceCUID)){
                continue;
            }
            Neo4jRetStruct ansTier = neo_q.getConceptTier(BestCUID,replaceCUID,TUI,answers.get(i));
            if(ansTier != null) {
                usedCUIDS.add(replaceCUID);
                bansTierList.get(ansTier.tier).add(ansTier);
            }
        }
        return bansTierList;
    }

    /**
     * Remove the words with the same cuid, and words that are in the question.
     * @param answers
     * @return
     */
    private static ArrayList<TypeStoreData> removeSameCUID(ArrayList<TypeStoreData> answers, String BestCUID) {
        ArrayList<TypeStoreData> l_ans = new ArrayList<>(answers);
        for (int i = 0; i < l_ans.size(); i++) {
            if (l_ans.get(i).CUID.equals(BestCUID)) {
                l_ans.remove(i);
                i--;
            }
        }
        return l_ans;
    }

    private static ArrayList<BadAns> pureGraphSearch(String BestCUID, Neo4jQuery neo_q, int subC, ArrayList<String> usedCUIDS, String semType) {
        ArrayList<BadAns> BadAnswers = fillVoidWithGraph(BestCUID, neo_q, subC,100,0,usedCUIDS, semType);
        if(BadAnswers!=null && BadAnswers.size()<minBans) {
            bansMap.put(BestCUID,BadAnswers);
        }else{
            bansMap.put(BestCUID,null);
        }
        return BadAnswers;
    }

    public static void addBans(ArrayList<BadAns> BadAnswers,BadAns badAnswer){
        //OTHER CONSTRAINTS
        BadAnswers.add(badAnswer);
    }

    public static void addAllBans(ArrayList<BadAns> BadAnswers, ArrayList<BadAns> candidates){
        for(int i=0; i<candidates.size(); i++){
            addBans(BadAnswers,candidates.get(i));
        }
    }




    //DEPRECIATED
//    public static void cleanQuestion(String utterance, String topPhrase,List<String> matched, ArrayList<BadAns> bansArray, MedicalQuestion medQ, String parseURL) throws Exception {
//        ArrayList<String> badAnsPhrases = new ArrayList<>();
//        for(int i=0; i<bansArray.size();i++){
//            badAnsPhrases.add(bansArray.get(i).subject);
//        }
//
//        System.out.println("utterance: "+utterance);
//        System.out.println("topPhrase: "+topPhrase);
//        System.out.println("matched: "+matched);
//
//
//        List<String> bas = FalseChoiceInflect.getBadChoicesString(utterance,topPhrase,matched, badAnsPhrases, parseURL);
//        //medQ.badAnswerPhrases = new String[bas.size()];
//        for(int i=0; i<bas.size();i++){
//            //medQ.badAnswerPhrases[i] = bas.get(i);
//            medQ.bansArray.get(i).text = bas.get(i);
//        }
//        String ans = utterance;
//        medQ.hasContext=(ans.replace(topPhrase,"").length()!=0);
//    }

    /**
     * Algorithm of selecting the best bad answer candidates.
     * Follows the following flow
     *      return if in cache
     *
     *      Get all cui's from the document that has the same semtype of our answer's cui
     *      Use neo4j to classify the candidates into tiers 0-3 (lower = better)
     *
     *      let parents = [];
     *      let bestCandidates = [];
     *      for x = 0, x < 4:
     *          let candidatesX = select all candidates of tier x
     *          sortByClosestComplexityToAnswer(candidatesX)
     *          bestCandidates.addAll(candidatesX);   *Add all document replacement candidates first
     *
     *          if( bestCandidates.length >= 3)
     *              return bestCandidates;
     *
     *          for all candidateX in candidatesX:
     *              parent.add(candidateX.parent)     * A candidate's parent is intermediate node between the subject and the candidate.
     *                                                * in other words the parent node is how the replacement candidate is related to the answer.
     *
     *          for all parents:
     *              let parent = parents.pop(0);
     *              let siblings = getCandFromPar(parent,answer)   *use neo4j to find any node that has acceptable relations with specified parent and answer.
     *              bestCandidates.addAll(siblings);
     *              if( bestCandidates.length >=3)
     *                  return bestCandidates
     *
     *          bestCandidates.addAll(fillVoidWithGraph(...))       *Try finding any good candidate using only the neo4j graph
     *
     *          return bestCandidates
     *
     * @param typeStore
     * @param BestCUID
     * @param neo_q
     * @param q
     * @param ansPhrase
     * @param subC
     * @return
     */
   // public static ArrayList<BadAns> cascadingTierBansGen(TypeStoreGen typeStore, String BestCUID, Neo4jQuery neo_q, MedicalQuestion q, String ansPhrase, int subC, ThreadNBloc curThread,SessionManager sm) {
//
//        //Check if fake answers were already generated from before. If so return those results
//        if(bansMap.containsKey(BestCUID)){
//            return getCachedCUI(BestCUID);
//        }
//
//        ArrayList<String> usedCUIDS = new ArrayList<>();
//        usedCUIDS.add(BestCUID);
//
//        //Get list of words with the same semtype as the subject
//        ArrayList<TypeStoreData> answers = typeStore.typeStore.get(q.replacementSemType);
//
//        //If list does not exist, perform a pureGraph search for candidates.
//        if(answers == null){
//            return pureGraphSearch(BestCUID,neo_q,subC,usedCUIDS,q.replacementSemType);
//        }
//
//        //Initialize data structures
//        ArrayList<BadAns> BadAnswers = new ArrayList<>();
//        answers = removeSameCUID(answers, BestCUID);
//        String TUI = SemTUI.getTUI(q.replacementSemType);
//        List<List<Neo4jRetStruct>> bansTierList = genBansTierList(answers, usedCUIDS, BestCUID, TUI, neo_q);
//
//        //Iterate through the list of tiers
//        for(int i=0; i<docRepTierLimit;i++){
//
//            //ShoeHorned graphPure(0) > docRep(3) TODO: create and array of official tier list [ combining both docRep and pureG ]
//            if(i==3){
//                addBansWithTopPureGraph(BadAnswers,BestCUID, neo_q, subC, usedCUIDS, q.replacementSemType);
//                if(BadAnswers.size()>= maxBans){ break;}
//            }
//
//            List<Neo4jRetStruct> tierCands = bansTierList.get(i);   //all the docRep candidates
//            ArrayList<String[]> parentRefs = new ArrayList<>();     //list of parentRefs to be used for intermediate searches
//            ArrayList<BadAns> finalCands = new ArrayList<>();       //candidates of this tier that passes all threshes
//
//            for(int j=0; j<tierCands.size();j++){
//                Neo4jRetStruct retStruct = tierCands.get(j);
//                String[] parentDetails = new String[2];
//                parentDetails[0] = retStruct.parentCUI;
//                parentDetails[1] = retStruct.parentTUI;
//                String origin = "DOCREP, TIER: "+i+"   ("+retStruct.ansGraphName+")-("+retStruct.parentStr+")-("+retStruct.repGraphName+")"+retStruct.notation;
//                String path = "Parent: "+retStruct.parentCUI;
//                BadAns bans = new BadAns(retStruct.answerData.text,retStruct.repCUI,origin,path,retStruct.bonusRank, retStruct.queryString);
//                bans.graphName = retStruct.repGraphName;
//                bans.ansName = ansPhrase;
//                bans.parentDetails = parentDetails;
//                if(bans.phraseComplexity < MedicalWeights.bansComplexityThresh) {
//                    finalCands.add(bans);
//                    usedCUIDS.add(retStruct.repCUI);
//                }
//            }
//
//            sort(finalCands,subC);
//
//            int k=0;
//            while( BadAnswers.size() < maxBans && k<finalCands.size() ){
//                sm.notes.makeTally("ReqDocTier["+i+"]");
//                addBans(BadAnswers,finalCands.get(k));
//                parentRefs.add(finalCands.get(k).parentDetails);
//                k++;
//            }
//
//            if(BadAnswers.size()>= maxBans){ break;}
//
//            //Using the parent of the answers on this tier to generate other candidates that should be within this tier
//            for(int j=0; j<parentRefs.size(); j++){
//                String pCUI = parentRefs.get(j)[0];
//                String pTUI = parentRefs.get(j)[1];
//                ArrayList<BadAns> bans = neo_q.getCandidatesWithParent(BestCUID,pCUI,pTUI,TUI,usedCUIDS, (maxBans - BadAnswers.size()),i,subC);
//                addAllBans(BadAnswers,bans);
//                sm.notes.makeTally("Parent["+i+"]["+neo_q.reqs_parent_titles[j]+"]");
//                if(BadAnswers.size() >= maxBans){ break; }
//            }
//            if(BadAnswers.size() >= maxBans){ break; }
//        }
//
//        //default pureGraph search
//        if(BadAnswers.size()< maxBans){
//            ArrayList<BadAns> bans = fillVoidWithGraph(BestCUID, neo_q, subC,100,1,usedCUIDS, q.replacementSemType);
//            for (int i = 0; i < bans.size(); i++) {
//                addBans(BadAnswers,bans.get(i));
//            }
//        }
//
//
//        if(BadAnswers.size()>=minBans){
//            bansMap.put(BestCUID,BadAnswers);
//        }else{
//            bansMap.put(BestCUID,null);
//        }
//
//        return BadAnswers;
   // }

//    private static ArrayList<BadAns> getCachedCUI(String BestCUID) {
//        if(bansMap.get(BestCUID)==null){
//            return null;
//        }
//        return (ArrayList<BadAns>)bansMap.get(BestCUID);
//    }
}
