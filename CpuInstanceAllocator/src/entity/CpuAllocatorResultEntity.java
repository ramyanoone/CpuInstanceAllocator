package entity;

import java.util.HashMap;
import java.util.Map;


/**
 * CpuAllocatorResultEntity is used to store result Data
 * 
 * @author Ramya N
 *
 */
public class CpuAllocatorResultEntity {
	private String region;
	private String totalCost;
	private Map<String, Integer> servers = new HashMap<>();

	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public Map<String, Integer> getServers() {
		return servers;
	}
	public void setServers(Map<String, Integer> serverMap) {
		this.servers = serverMap;
	}
	public String getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}
}
