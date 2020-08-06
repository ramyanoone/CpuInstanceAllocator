package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import entity.CpuAllocatorResultEntity;

/**
 * CpuInstanceAllocatorMain class is used to create optimized cpu instance allocator
 * 
 * @author Ramya N
 *
 */
public class CpuInstanceAllocatorMain {
	public List<CpuAllocatorResultEntity> getCosts(int hours, int cpus, double price){
		
		Map<String, Map<String, Double>> regionWiseCostMap = getRegionWiseCostData();
		List<CpuAllocatorResultEntity> cpuAllocatorResultEntityList = new ArrayList<>();
		if(cpus!=0 && price==0) {
			findCostWithCPUCount(regionWiseCostMap, cpus, hours, cpuAllocatorResultEntityList);
		} else if(cpus==0 && price!=0) {
			findCostWithMinPrice(regionWiseCostMap, price, hours, cpuAllocatorResultEntityList);
		} else if(cpus!=0 && price!=0) {
			findCostWithMinPriceMinCpu(regionWiseCostMap, cpus, price, hours, cpuAllocatorResultEntityList);
		}
		Collections.sort(cpuAllocatorResultEntityList, Comparator.comparing(CpuAllocatorResultEntity :: getTotalCost));
		return cpuAllocatorResultEntityList;
	}

	/**
	 * findCostWithMinPriceMinCpu method is used to calculate costs using minimum price and minimum Cpu's needed
	 * 
	 * @param regionWiseCostMap
	 * @param cpus
	 * @param price
	 * @param hours
	 * @param cpuAllocatorResultEntityList
	 */
	private void findCostWithMinPriceMinCpu(Map<String, Map<String, Double>> regionWiseCostMap, int cpus, double price,
			int hours, List<CpuAllocatorResultEntity> cpuAllocatorResultEntityList) {
		Set<String> regionSet = regionWiseCostMap.keySet();
		regionSet.stream().forEach(region -> {
			Map<String, Double> costMap = regionWiseCostMap.get(region);
			CpuAllocatorResultEntity cpuAllocatorResultEntity = new CpuAllocatorResultEntity();
			cpuAllocatorResultEntity.setRegion(region);
			Map<String,Integer> serversCountMap = new HashMap<>();
			int cpuCount = cpus;
			double minPrice = price;
			double totalCost = 0;
			while(cpuCount>0 && minPrice>0) {
				if(cpuCount>=32 && costMap.containsKey("10xlarge") && minPrice>=costMap.get("10xlarge")*hours) {
					int largeCpu_10x_count = Math.min(cpuCount/32, (int) (minPrice/(costMap.get("10xlarge")*hours)));
					cpuCount = cpuCount - largeCpu_10x_count*32;
					minPrice = minPrice - largeCpu_10x_count*costMap.get("10xlarge")*hours;
					serversCountMap.put("10xlarge", serversCountMap.getOrDefault("10xlarge", 0)+largeCpu_10x_count);
					totalCost = totalCost + costMap.get("10xlarge")*largeCpu_10x_count*hours;
				} else if (cpuCount>=16 && costMap.containsKey("8xlarge") && minPrice>=costMap.get("8xlarge")*hours) {
					int largeCpu_8x_count = Math.min(cpuCount/16, (int) (minPrice/(costMap.get("8xlarge")*hours)));
					cpuCount = cpuCount- largeCpu_8x_count*16;
					minPrice = minPrice - largeCpu_8x_count*costMap.get("8xlarge")*hours;
					serversCountMap.put("8xlarge", serversCountMap.getOrDefault("8xlarge", 0)+largeCpu_8x_count);
					totalCost = totalCost + costMap.get("8xlarge")*largeCpu_8x_count*hours;
				} else if (cpuCount>=8 && costMap.containsKey("4xlarge") && minPrice>=costMap.get("4xlarge")*hours) {
					int largeCpu_4x_count = Math.min(cpuCount/8, (int) (minPrice/(costMap.get("4xlarge")*hours)));
					cpuCount = cpuCount- largeCpu_4x_count*8;
					minPrice = minPrice - largeCpu_4x_count*costMap.get("4xlarge")*hours;
					serversCountMap.put("4xlarge", serversCountMap.getOrDefault("4xlarge", 0)+largeCpu_4x_count);
					totalCost = totalCost + costMap.get("4xlarge")*largeCpu_4x_count*hours;
				} else if (cpuCount>=4 && costMap.containsKey("2xlarge") && minPrice>=costMap.get("2xlarge")*hours) {
					int largeCpu_2x_count = Math.min(cpuCount/4, (int) (minPrice/(costMap.get("2xlarge")*hours)));
					cpuCount = cpuCount - largeCpu_2x_count*4;
					minPrice = minPrice - largeCpu_2x_count*costMap.get("2xlarge")*hours;
					serversCountMap.put("2xlarge", serversCountMap.getOrDefault("2xlarge", 0)+largeCpu_2x_count);
					totalCost = totalCost + costMap.get("2xlarge")*largeCpu_2x_count*hours;
				} else if (cpuCount>=2 && costMap.containsKey("xlarge") && minPrice>=costMap.get("xlarge")*hours) {
					int largeCpu_x_count = Math.min(cpuCount/2, (int) (minPrice/(costMap.get("xlarge")*hours)));
					cpuCount = cpuCount - largeCpu_x_count*2;
					minPrice = minPrice - largeCpu_x_count*costMap.get("xlarge")*hours;
					serversCountMap.put("xlarge", serversCountMap.getOrDefault("xlarge", 0)+largeCpu_x_count);
					totalCost = totalCost + costMap.get("xlarge")*largeCpu_x_count*hours;
				} else if (cpuCount>=1 && costMap.containsKey("large") && minPrice>=costMap.get("large")*hours) {
					int largeCpu_count = Math.min(cpuCount, (int) (minPrice/(costMap.get("large")*hours)));
					cpuCount = cpuCount - largeCpu_count;
					minPrice = minPrice - largeCpu_count*costMap.get("large")*hours;
					serversCountMap.put("large", serversCountMap.getOrDefault("large", 0)+largeCpu_count);
					totalCost = totalCost + costMap.get("large")*largeCpu_count*hours;
				} else {
					break;
				}
			}
			cpuAllocatorResultEntity.setServers(serversCountMap);
			Currency usdCurrency = Currency.getInstance("USD");
			cpuAllocatorResultEntity.setTotalCost(usdCurrency.getSymbol(Locale.US)+String.format("%.2f", totalCost));
			cpuAllocatorResultEntityList.add(cpuAllocatorResultEntity);
		});		
	}

	/**
	 * findCostWithMinPrice method is used to calculate cost using minimum price
	 * 
	 * @param regionWiseCostMap
	 * @param price
	 * @param hours
	 * @param cpuAllocatorResultEntityList
	 */
	private void findCostWithMinPrice(Map<String, Map<String, Double>> regionWiseCostMap, double price, int hours,
			List<CpuAllocatorResultEntity> cpuAllocatorResultEntityList) {
		Set<String> regionSet = regionWiseCostMap.keySet();
		regionSet.stream().forEach(region -> {
			Map<String, Double> costMap = regionWiseCostMap.get(region);
			CpuAllocatorResultEntity cpuAllocatorResultEntity = new CpuAllocatorResultEntity();
			cpuAllocatorResultEntity.setRegion(region);
			Map<String,Integer> serversCountMap = new HashMap<>();
			double minPrice = price;
			double totalCost =0;
			while(minPrice>0) {
				if(costMap.containsKey("10xlarge") && minPrice>=costMap.get("10xlarge")*hours) {
					int largeCpu_10x_count = (int) (minPrice/(costMap.get("10xlarge")*hours));
					minPrice = minPrice - largeCpu_10x_count*costMap.get("10xlarge")*hours;
					serversCountMap.put("10xlarge", serversCountMap.getOrDefault("10xlarge", 0)+largeCpu_10x_count);
					totalCost = totalCost + costMap.get("10xlarge")*largeCpu_10x_count*hours;
				} else if (costMap.containsKey("8xlarge") && minPrice>=costMap.get("8xlarge")*hours) {
					int largeCpu_8x_count = (int) (minPrice/(costMap.get("8xlarge")*hours));
					minPrice = minPrice - largeCpu_8x_count*costMap.get("8xlarge")*hours;
					serversCountMap.put("8xlarge", serversCountMap.getOrDefault("8xlarge", 0)+largeCpu_8x_count);
					totalCost = totalCost + costMap.get("8xlarge")*largeCpu_8x_count*hours;
				} else if (costMap.containsKey("4xlarge") && minPrice>=costMap.get("4xlarge")*hours) {
					int largeCpu_4x_count = (int) (minPrice/(costMap.get("4xlarge")*hours));
					minPrice = minPrice - largeCpu_4x_count*costMap.get("4xlarge")*hours;
					serversCountMap.put("4xlarge", serversCountMap.getOrDefault("4xlarge", 0)+largeCpu_4x_count);
					totalCost = totalCost + costMap.get("4xlarge")*largeCpu_4x_count*hours;
				} else if (costMap.containsKey("2xlarge") && minPrice>=costMap.get("2xlarge")*hours) {
					int largeCpu_2x_count = (int) (minPrice/(costMap.get("2xlarge")*hours));
					minPrice = minPrice - largeCpu_2x_count*costMap.get("2xlarge")*hours;
					serversCountMap.put("2xlarge", serversCountMap.getOrDefault("2xlarge", 0)+largeCpu_2x_count);
					totalCost = totalCost + costMap.get("2xlarge")*largeCpu_2x_count*hours;
				} else if (costMap.containsKey("xlarge") && minPrice>=costMap.get("xlarge")*hours) {
					int largeCpu_x_count = (int) (minPrice/(costMap.get("xlarge")*hours));
					minPrice = minPrice - largeCpu_x_count*costMap.get("xlarge")*hours;
					serversCountMap.put("xlarge", serversCountMap.getOrDefault("xlarge", 0)+largeCpu_x_count);
					totalCost = totalCost + costMap.get("xlarge")*largeCpu_x_count*hours;
				} else if (costMap.containsKey("large") && minPrice>=costMap.get("large")*hours) {
					int largeCpu_count = (int) (minPrice/(costMap.get("large")*hours));
					minPrice = minPrice - largeCpu_count*costMap.get("large")*hours;
					serversCountMap.put("large", serversCountMap.getOrDefault("large", 0)+largeCpu_count);
					totalCost = totalCost + costMap.get("large")*largeCpu_count*hours;
				} else {
					break;
				}
			}
			cpuAllocatorResultEntity.setServers(serversCountMap);
			Currency usdCurrency = Currency.getInstance("USD");
			cpuAllocatorResultEntity.setTotalCost(usdCurrency.getSymbol(Locale.US)+String.format("%.2f", totalCost));
			cpuAllocatorResultEntityList.add(cpuAllocatorResultEntity);
		});
		
	}

	/**
	 * findCostWithCPUCount method is used to calculate cost using minimum Cpu's needed
	 * 
	 * @param regionWiseCostMap
	 * @param cpus
	 * @param hours
	 * @param cpuAllocatorResultEntityList
	 */
	private void findCostWithCPUCount(Map<String, Map<String, Double>> regionWiseCostMap, int cpus, int hours, List<CpuAllocatorResultEntity> cpuAllocatorResultEntityList) {
		Set<String> regionSet = regionWiseCostMap.keySet();
		regionSet.stream().forEach(region -> {
			Map<String, Double> costMap = regionWiseCostMap.get(region);
			CpuAllocatorResultEntity cpuAllocatorResultEntity = new CpuAllocatorResultEntity();
			cpuAllocatorResultEntity.setRegion(region);
			Map<String,Integer> serversCountMap = new HashMap<>();
			int cpuCount = cpus;
			double totalCost = 0;
			while(cpuCount>0) {
				if(cpuCount>=32 && costMap.containsKey("10xlarge")) {
					int largeCpu_10x_count = cpuCount/32;
					cpuCount = cpuCount%32;
					serversCountMap.put("10xlarge", serversCountMap.getOrDefault("10xlarge", 0)+largeCpu_10x_count);
					totalCost = totalCost + costMap.get("10xlarge")*largeCpu_10x_count*hours;
				} else if (cpuCount>=16 && costMap.containsKey("8xlarge")) {
					int largeCpu_8x_count = cpuCount/16;
					cpuCount = cpuCount%16;
					serversCountMap.put("8xlarge", serversCountMap.getOrDefault("8xlarge", 0)+largeCpu_8x_count);
					totalCost = totalCost + costMap.get("8xlarge")*largeCpu_8x_count*hours;
				} else if (cpuCount>=8 && costMap.containsKey("4xlarge")) {
					int largeCpu_4x_count = cpuCount/8;
					cpuCount = cpuCount%8;
					serversCountMap.put("4xlarge", serversCountMap.getOrDefault("4xlarge", 0)+largeCpu_4x_count);
					totalCost = totalCost + costMap.get("4xlarge")*largeCpu_4x_count*hours;
				} else if (cpuCount>=4 && costMap.containsKey("2xlarge")) {
					int largeCpu_2x_count = cpuCount/4;
					cpuCount = cpuCount%4;
					serversCountMap.put("2xlarge", serversCountMap.getOrDefault("2xlarge", 0)+largeCpu_2x_count);
					totalCost = totalCost + costMap.get("2xlarge")*largeCpu_2x_count*hours;
				} else if (cpuCount>=2 && costMap.containsKey("xlarge")) {
					int largeCpu_x_count = cpuCount/2;
					cpuCount = cpuCount%2;
					serversCountMap.put("xlarge", serversCountMap.getOrDefault("xlarge", 0)+largeCpu_x_count);
					totalCost = totalCost + costMap.get("xlarge")*largeCpu_x_count*hours;
				} else if (cpuCount>=1 && costMap.containsKey("large")) {
					int largeCpu_count = cpuCount;
					cpuCount = 0;
					serversCountMap.put("large", serversCountMap.getOrDefault("large", 0)+largeCpu_count);
					totalCost = totalCost + costMap.get("large")*largeCpu_count*hours;
				} else {
					break;
				}
			}
			cpuAllocatorResultEntity.setServers(serversCountMap);
			Currency usdCurrency = Currency.getInstance("USD");
			cpuAllocatorResultEntity.setTotalCost(usdCurrency.getSymbol(Locale.US)+String.format("%.2f", totalCost));
			cpuAllocatorResultEntityList.add(cpuAllocatorResultEntity);
		});		
	}

	/**
	 * getRegionWiseCostData method is used to get region wise cost for each server
	 * 
	 * @return regionWiseCostMap
	 */
	private Map<String, Map<String, Double>> getRegionWiseCostData() {
		Map<String, Map<String, Double>> regionWiseCostMap = new HashMap<>();
		Map<String, Double> usEastRegionWiseCostMap = new HashMap<>();
		Map<String, Double> usWestRegionWiseCostMap = new HashMap<>();
		Map<String, Double> asiaRegionWiseCostMap = new HashMap<>();
		usEastRegionWiseCostMap.put("large" , 0.12);
		usEastRegionWiseCostMap.put("xlarge" ,0.23);
		usEastRegionWiseCostMap.put("2xlarge" , 0.45);
		usEastRegionWiseCostMap.put("4xlarge" , 0.774);
		usEastRegionWiseCostMap.put("8xlarge" , 1.4);
		usEastRegionWiseCostMap.put("10xlarge" , 2.82);
		
		usWestRegionWiseCostMap.put("large" , 0.14);
		usWestRegionWiseCostMap.put("2xlarge" , 0.413);
		usWestRegionWiseCostMap.put("4xlarge" , 0.89);
		usWestRegionWiseCostMap.put("8xlarge" , 1.3);
		usWestRegionWiseCostMap.put("10xlarge" , 2.97);
		
		asiaRegionWiseCostMap.put("large" , 0.11);
		asiaRegionWiseCostMap.put("xlarge" , 0.20);
		asiaRegionWiseCostMap.put("4xlarge" , 0.67);
		asiaRegionWiseCostMap.put("8xlarge" , 1.18);

		regionWiseCostMap.put("us-east", usEastRegionWiseCostMap);
		regionWiseCostMap.put("us-west", usWestRegionWiseCostMap);
		regionWiseCostMap.put("asia", asiaRegionWiseCostMap);
		return regionWiseCostMap;
	}
}
