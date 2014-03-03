package game;

import java.util.List;

public class CollectionsUtil {
	public static boolean arrayContains(int[] array, int element){
		for(int i = 0; i < array.length; i++){
			if(array[i] == element){
				return true;
			}
		}
		return false;
	}
	
	public static int getIndexOf(int[] array, int element){
		for(int i = 0; i < array.length; i++){
			if(array[i] == element){
				return i;
			}
		}
		return -1;
	}
	
	public static int[] toArray(List<Integer> list){
		int[] a = new int[list.size()];
		for(int i = 0; i < list.size(); i++){
			a[i] = list.get(i);
		}
		return a;
	}
}
