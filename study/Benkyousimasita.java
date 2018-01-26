/*
 * USAGE
 * : CHANGE Jikan.inputList to sum of your subject name
 *   (in my case, "n"+"j"+"a")
 *   shorter is better, because these words are what you have type
 * : CHANGE Jikan.kamokuList to list of your subject full name
 *   DO NOT change "\ntotal" element
 *   you'd better put some space next to subject name for beauty
 * : INPUT FORM sould be subject_name:hour:minute
 *   or you can just type subject_name:hour or subject_name:minute
 *   but in this case, minute sould be equal to or more than 10,
 *   and hour shold be less than 10
 * 
 * PURPOSE
 * : This code is made for people who study everyday,
 *   and strictly check the study time,
 *   but feel tricky to calculate the total study time,
 *   and that's me
 * 
 * VERSION
 * : This is v1.0
 *   It can be upgraded when needed for me
 * : PLAN is saving the data in file to check previous days
 * 
 * MADE
 * : by cy118 on 18/01/16
 * 
 */

import java.util.*;

class Jikan {
    static String inputList = "nja";
    static String[] kwamokuList = {"nihongo  ", "java     ", "algorithm", "\ntotal    "};
    HashMap jikanList = new HashMap(10);

    Jikan() {
        this(new String[]{"n:0:0", "j:0:0", "a:0:0"});
    }

    Jikan(String[] list) {
        int total = 0;
        for (int i = 0; i < list.length; ++i) {
            if (list[i] == null)  break;
            String input = list[i];
            Scanner s = new Scanner(input).useDelimiter(":");
            
            int kamoku = inputList.indexOf(s.next());
            int ji = s.nextInt();
            int hunn;
            try {
                hunn = s.nextInt();
            } catch (Exception e) {
                if (ji > 10) {
                    hunn = ji;
                    ji = 0;
                } else {
                    hunn = 0;
                }
            }
            total += ji*60 + hunn;

            if (!jikanList.containsKey(kamoku)) {
                jikanList.put(kamoku, 60*ji+hunn);
            } else {
                int prev = (int)jikanList.get(kamoku);
                jikanList.put(kamoku, 60*ji+hunn+prev);
            }

            if (!jikanList.containsKey(0)) {
                jikanList.put(0, 0);
            }
            if (!jikanList.containsKey(1)) {
                jikanList.put(1, 0);
            }
            if (!jikanList.containsKey(2)) {
                jikanList.put(2, 0);
            }
        }
        jikanList.put(3, total); 
    }

    String getJikan() {
        Iterator it = jikanList.entrySet().iterator();
        String ret = "";

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            ret += kwamokuList[(int)entry.getKey()] + ": ";
            
            int tmp = (int)entry.getValue();
            String ji = "" + tmp/60;
            String hunn = "" + tmp%60;
            ret += ji + " jikan " + hunn + " hunn\n";
        }
        return ret;
    }
}

class Benkyousimasita {
    static String[] getInput() {
        String[] ret = new String[10];
        int idx = 0;
        Scanner s = new Scanner(System.in);
        System.out.println("-----Kinou benkyousimasitaka?-----");
        System.out.println("n: nihongo   j: java   a:algorithm");
        System.out.println("       tsukaikata - n:1:50        ");
        System.out.println("     benkyo jikan mosikuha q");

        do {
            System.out.print(">> ");
            String tmp = s.nextLine().trim();
            if (tmp.equals("q")) {
                System.out.println();
                return ret;
            }
            ret[idx++] = tmp;
        } while (true);
    }

    public static void main(String[] args) {
        Jikan j = new Jikan(getInput());
        String str = j.getJikan();
        System.out.println(str);    
    }
}