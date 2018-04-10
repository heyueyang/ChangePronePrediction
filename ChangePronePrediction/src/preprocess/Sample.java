package preprocess;
import Util.Config;

import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Random;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class Sample{
	 private static String className = "bug_introducingCopy";//"change_prone";
	 public Sample(String claName){
		 className = claName;
	 }
	 public Sample(){
	 }
	 
	 public static Instances AntiUnderSample(Instances init, double samRatio) throws Exception{
		 double ratio = samRatio;
		 int numAttr = init.numAttributes();
			int numInstance = init.numInstances();

			FastVector attInfo = new FastVector();
			for (int i = 0; i < numAttr; i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}

			Instances NoInstances = new Instances("No", attInfo, numInstance);

			NoInstances.setClass(NoInstances.attribute(className));

			Instances YesInstances = new Instances("yes", attInfo, numInstance);
			YesInstances.setClass(YesInstances.attribute(className));

			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			
			int numYes = 0;
			int numNo = 0;
			
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 0) { // yes
					NoInstances.add(temp);
					numNo++;
				} else {
					YesInstances.add(temp);
					numYes++;
				}
			}
			
			Instances res;
			if (numYes > numNo) {
				if ((double)(numNo/numYes) <= ratio) {
					return init;
				}
				res = excuteSample(YesInstances, NoInstances, ratio);
				
			} else {
				if ((double)(numYes/numNo) <= ratio) {
					return init;
				}
				res = excuteSample(NoInstances, YesInstances, ratio);
			}
			return res;

		 /*
			Instances res = new Instances(ins);
			res.delete();
			int valueCnt[] = {0,0};
			int attNum = ins.numAttributes();
			int insNum = ins.numInstances();
			int[] label = new int[insNum];
			ins.setClassIndex(attNum-1);
			int temp = 0;
			for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				valueCnt[temp]++;
				label[i] = temp;
			}
			int anomCnt = (valueCnt[0] > valueCnt[1]) ? valueCnt[1] : valueCnt[0];
	    	int anomLab = (valueCnt[0] > valueCnt[1]) ? 1 : 0;
	    	
	    	java.util.Random r=new java.util.Random();
	    	int[] posInd = new int[anomCnt];
	    	int[] negInd = new int[insNum - anomCnt];
	    	int t = 0, p = 0;
	    	int samNum = 0;
	    	for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				if(label[i] == anomLab){
					posInd[t++] = i;
					
				}else{
					negInd[p++] = i;
				}	
			}
	    	//sampling in positive index
	    	if(((double)anomCnt/(double)insNum) <= samRatio){
	    		System.out.println(" (anomCnt"+anomCnt+")/insNum("+insNum+") "+((double)anomCnt/(double)insNum)+"<= samRatio "+samRatio);
	    		res = ins;
	    	}else{
		    	samNum = (int) ((insNum-anomCnt)*samRatio);
		    	if(samNum <= anomCnt){
			    	for(int i =0; i < samNum; i++){
			    		//System.out.println(posInd[r.nextInt(anomCnt)]);
			    		res.add(ins.instance(posInd[r.nextInt(anomCnt)]));
			    	}
			    	for(int i =0; i < negInd.length; i++){
			    		res.add(ins.instance(negInd[i]));
			    	} 
			    	
		    	}else{
		    		throw new Exception("samNum "+samNum+" must be smaller than number of positive instances "+anomCnt);
		    	}
		    	
	    	}
	    	String underPath = Config.select_folder + this.file.substring( 0,  file.lastIndexOf(".")) + "_undersample" + ".arff";
            System.out.println("===UnderSample Path==="+underPath);
            System.out.println("===minority num("+anomLab+"):"+anomCnt+" ===");
            System.out.println("===new minority num("+anomLab+"):"+samNum+" ===");
            ArffSaver saver = new ArffSaver(); 
    		saver.setInstances(res);  
    	    saver.setFile(new File(underPath));  
    	    saver.writeBatch(); 
	    	return res;*/
	    	
		 }
	 
	 public static Instances AntiOverSample(Instances init, double overTimes) throws Exception{
		 double ratio = overTimes;
		 FastVector attInfo = new FastVector();
			for (int i = 0; i < init.numAttributes(); i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}
			Instances YesInstances = new Instances("DefectSample1", attInfo,
					init.numInstances());// 鏉╂瑩鍣烽惃鍕灥婵顔愰柌蹇涙付鐟曚焦鏁為幇蹇ョ礉娑撳秷顪呯亸蹇庣啊閵嗭拷
			YesInstances.setClass(YesInstances.attribute(className));

			// YesInstances.setClassIndex(init.numAttributes() - 1);
			// 閺堫亣鍏樼紒鐔剁閻ㄥ嫬鐨㈢猾缁樼垼缁涘彞缍旀稉鐑樻付閸氬簼绔存稉顏勭潣閹嶇礉閸欘垵鍏橈拷?鑹板毀鐠侊紕鐣绘稉濠勬畱婢跺秵娼呴敍灞炬箒瀵板懏鏁兼潻娑栵拷
			Instances Noinstances = new Instances("DefectSample2", attInfo,
					init.numInstances());
			Noinstances.setClass(Noinstances.attribute(className));
			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			int numInstance = init.numInstances();
			int numYes = 0;
			int numNo = 0;
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 1) { // weka閻ㄥ嫬鍞撮柈銊ワ拷楠炴湹绗夋稉搴＄潣閹呮畱閸婅偐娴夛拷?鐟扮安閿涘苯寮懓鍍縠ka api閵嗭拷
					YesInstances.add(temp);
					numYes++;
				} else // clear change
				{
					Noinstances.add(temp);
					numNo++;
				}
			}
			// 婵″倹鐏夐弫浼村櫤閻╁摜鐡戦敍灞界杽闂勫懍绗傞弰顖涚梾閺堝澧界悰宀冪箖闁插洦鐗遍惃鍕╋拷
			Instances res;
			if (numYes > numNo) {
				if ((double)(numNo/numYes) <= ratio) {
					return init;
				}
				res = excuteSample(Noinstances, YesInstances, 1/ratio);
			} else {
				if ((double)(numYes/numNo) <= ratio) {
					return init;
				}
				res = excuteSample(YesInstances, Noinstances, 1/ratio);
			}
			return res;
		    /*
			Instances res = ins;
			int valueCnt[] = {0,0};
			int attNum = ins.numAttributes();
			int insNum = ins.numInstances();
			int[] label = new int[insNum];
			ins.setClassIndex(attNum-1);
			int temp = 0;
			for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				valueCnt[temp]++;
				label[i] = temp;
			}
			int majorityCnt = (valueCnt[0] > valueCnt[1]) ? valueCnt[0] : valueCnt[1];
	    	int majorityLab = (valueCnt[0] > valueCnt[1]) ? 0 : 1;
	    	
	    	java.util.Random r=new java.util.Random();
	    	int[] posInd = new int[insNum-majorityCnt];
	    	int[] negInd = new int[majorityCnt];
	    	int t = 0, p = 0;
	    	
	    	for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				if(label[i] == majorityLab){
					negInd[t++] = i;
					
				}else{
					posInd[p++] = i;
				}	
			}
	    	//sampling in positive index
	    	int samNum = (int) (majorityCnt*(overTimes-1));
		    for(int i =0; i < samNum; i++){
		    	//System.out.println(posInd[r.nextInt(anomCnt)]);
		    	res.add(ins.instance(negInd[r.nextInt(majorityCnt)]));
		    }
		    String overPath = Config.select_folder + this.file.substring( 0,  file.lastIndexOf(".")) + "_oversample" + ".arff";
            System.out.println("===OverSample Path==="+overPath);
            System.out.println("===majority num("+majorityLab+"):"+majorityCnt+" ===");
            System.out.println("===new majority num("+majorityLab+"):"+samNum+" ===");
            ArffSaver saver = new ArffSaver(); 
    		saver.setInstances(res);  
    	    saver.setFile(new File(overPath));  
    	    saver.writeBatch(); 
	    	
	    	return res;*/
		 }
	 
	 /**
		 * 鏉╁洭鍣伴弽閿嬫煙濞夋洏锟�?
		 * 
		 * @param init
		 * @return
		 * @throws IOException
		 */
		public static Instances OverSample(Instances init) throws IOException {
			FastVector attInfo = new FastVector();
			for (int i = 0; i < init.numAttributes(); i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}
			Instances YesInstances = new Instances("DefectSample1", attInfo,
					init.numInstances());// 鏉╂瑩鍣烽惃鍕灥婵顔愰柌蹇涙付鐟曚焦鏁為幇蹇ョ礉娑撳秷顪呯亸蹇庣啊閵嗭拷
			YesInstances.setClass(YesInstances.attribute(className));

			// YesInstances.setClassIndex(init.numAttributes() - 1);
			// 閺堫亣鍏樼紒鐔剁閻ㄥ嫬鐨㈢猾缁樼垼缁涘彞缍旀稉鐑樻付閸氬簼绔存稉顏勭潣閹嶇礉閸欘垵鍏橈拷?鑹板毀鐠侊紕鐣绘稉濠勬畱婢跺秵娼呴敍灞炬箒瀵板懏鏁兼潻娑栵拷
			Instances Noinstances = new Instances("DefectSample2", attInfo,
					init.numInstances());
			Noinstances.setClass(Noinstances.attribute(className));
			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			int numInstance = init.numInstances();
			int numYes = 0;
			int numNo = 0;
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 1) { // weka閻ㄥ嫬鍞撮柈銊ワ拷楠炴湹绗夋稉搴＄潣閹呮畱閸婅偐娴夛拷?鐟扮安閿涘苯寮懓鍍縠ka api閵嗭拷
					YesInstances.add(temp);
					numYes++;
				} else // clear change
				{
					Noinstances.add(temp);
					numNo++;
				}
			}
			// 婵″倹鐏夐弫浼村櫤閻╁摜鐡戦敍灞界杽闂勫懍绗傞弰顖涚梾閺堝澧界悰宀冪箖闁插洦鐗遍惃鍕╋拷
			if (numYes == numNo) {
				return init;
			}
			Instances res;
			if (numYes > numNo) {
				res = excuteSample(YesInstances, Noinstances, 1);
			} else {
				res = excuteSample(Noinstances, YesInstances, 1);
			}
			return res;
		}

		/**
		 * 閹稿鍙庣紒娆忕暰閻ㄥ嫭鐦笟瀣箻鐞涘矁绻冮幎鑺ョ壉閵嗭拷
		 * 
		 * @param instances1
		 *            娑撹鐤勬笟瀣肠閿涘苯宓嗘笟婵囧祦閻ㄥ嫬鐤勬笟瀣肠閿涘奔绡冪亸杈ㄦЦ閸忋劑鍎存担璺ㄦ暏閻ㄥ嫬鐤勬笟瀣肠閵嗭拷
		 * @param instances2
		 *            閸擃垰鐤勬笟瀣肠閿涘奔绡冪亸杈ㄦЦ閻喐顒滐拷?鐐额攽闁插洦鐗遍惃鍕杽娓氬娉﹂妴锟�?		 * @param i
		 *            閹惰姤鐗遍崥搴＄繁閸掓壆娈戞稉宥呮倱閻ㄥ嫮琚弽鍥╊劮閻ㄥ嫭鐦笟瀣剁礉閸楄櫕濞婇弽宄版倵num(yesInstances)/num(noinstances)閻ㄥ嫭鐦笟瀣剁礉濞夈劍鍓伴敍锟�?		 *            閻㈠彉绨稉杞扮啊 閸旂娀锟界粙瀣碍鏉╂劘顢戦柅鐔峰閿涘本娓堕崥搴＄杽妤犲瞼绮ㄩ弸婊勫▕閺嶉攱妞傜拋鍓х枂娑擄拷閵嗭拷
		 */
		private static Instances excuteSample(Instances instances1,
				Instances instances2, double ratio) {
			int numSample = (int) Math.ceil(instances1.numInstances() * ratio); // 娴兼矮绗夋导姘辨暠娴滃骸鐤勬笟瀣殶鏉╁洤顧嬮懓灞界┛濠у喛锟�?
			int numNo = instances2.numInstances();
			Random rn = new Random();
			for (int i = 0; i < numSample; i++) {
				instances1.add(instances2.instance(rn.nextInt(numNo)));
			}
			return instances1;
		}

		/**
		 * 濞嗙娀鍣伴弽閿嬫煙濞夛拷
		 * @param init 閻€劋绨柌鍥ㄧ壉閻ㄥ嫬鐤勬笟瀣肠.
		 * @return
		 * @throws IOException
		 */
		public static Instances UnderSample(Instances init) throws IOException {
			int numAttr = init.numAttributes();
			int numInstance = init.numInstances();

			FastVector attInfo = new FastVector();
			for (int i = 0; i < numAttr; i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}

			Instances NoInstances = new Instances("No", attInfo, numInstance);

			NoInstances.setClass(NoInstances.attribute(className));

			Instances YesInstances = new Instances("yes", attInfo, numInstance);
			YesInstances.setClass(YesInstances.attribute(className));

			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			
			int numYes = 0;
			int numNo = 0;
			
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 0) { // yes
					NoInstances.add(temp);
					numNo++;
				} else {
					YesInstances.add(temp);
					numYes++;
				}
			}
			if (numYes == numNo) {
				return init;
			}
			Instances res;
			if (numYes > numNo) {
				res = excuteSample(NoInstances, YesInstances, 1);
			} else {
				res = excuteSample(YesInstances, NoInstances, 1);
			}
			return res;
		}

		
		public static Instances SmoteSample(Instances ins, double ratio) throws Exception
		{
			int rat = 0;
			int classIndex = ins.classIndex();
			int numInstance = ins.numInstances();
			int numYes = 0;
			int numNo = 0;
			
			for (int i = 0; i < numInstance; i++) {
				Instance temp = ins.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 0) { // yes
					numNo++;
				} else {
					numYes++;
				}
			}
			if (numYes == numNo) {
				rat = 100;
			}
			if (numYes > numNo) {
				rat = numYes/numNo;
			} else {
				rat = numNo/numYes;
			}
			
			weka.filters.supervised.instance.SMOTE smote = new  SMOTE();
			//smote.setPercentage(ratio*100);
			//smote.setPercentage(rat*100);
			//System.out.println("somte percentage : " + smote.getPercentage());
			ins.setClassIndex(ins.numAttributes()-1);
			smote.setInputFormat(ins);
			Instances res = Filter.useFilter(ins, smote);
			return res;
			
		}
		
		public Instances RandomSample(Instances init, double ratio) {
			int numAttr = init.numAttributes();
			int numInstance = init.numInstances();
			int totalNum = (int) (numInstance * ratio);
			
			FastVector attInfo = new FastVector();
			for (int i = 0; i < numAttr; i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}
			Instances res = new Instances("Res", attInfo, totalNum);
			Random rn = new Random();
			for (int i = 0; i <totalNum; i++) {
					res.add(init.instance(rn.nextInt(numInstance)));
			}
			res.setClass(res.attribute(className));
			return res;
		}
	
}