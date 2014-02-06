package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class WaveReward {
	public List<TowerType> towers = new ArrayList<>();
//	public List<ItemType> items = new ArrayList<>();
	public HashMap<ItemType, Double> items = new HashMap<>();
	public List<ExclusiveItems> exclusiveItems = new ArrayList<>();
	public int money;
	
	public List<ItemType> getAllRandomItems(){
		List<ItemType> itemsToReturn = new ArrayList<>();
		for(ItemType item : items.keySet()){
			double d = new Random().nextDouble();
			if(d < items.get(item)){
				itemsToReturn.add(item);
			}
		}
		
		for(ExclusiveItems exclusive : exclusiveItems){
			ItemType exclusiveItem = exclusive.getRandomItem();
			if(exclusiveItem != null){
				itemsToReturn.add(exclusiveItem);
			}
		}
		
		return itemsToReturn;
	}
	
	public static class ExclusiveItems{
		public ExclusiveItems(HashMap<ItemType, Double> map){
			itemProbabilities = map;
		}
		public HashMap<ItemType, Double> itemProbabilities = new HashMap<>();
		public ItemType getRandomItem(){
			double d = new Random().nextDouble();
			double accumulatedProb = 0;
			for(ItemType item : itemProbabilities.keySet()){
				accumulatedProb += itemProbabilities.get(item);
				if(d < accumulatedProb){
					return item;
				}
			}
			return null;
		}
		
		public String toString(){
			return itemProbabilities.toString();
		}
	}
	
	public String toString(){
		return items.toString() + "\n" + exclusiveItems.toString();
	}
}
