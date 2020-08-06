package test;

import java.util.List;

import org.junit.jupiter.api.Test;

import entity.CpuAllocatorResultEntity;
import main.CpuInstanceAllocatorMain;

class CpuInstanceAllocatorTest {
	
	CpuInstanceAllocatorMain cpuMain = new CpuInstanceAllocatorMain();

	@Test
	void testGetCostScenario1() {
		System.out.println("Scenario-1 :");
		List<CpuAllocatorResultEntity> entityList = cpuMain.getCosts(24, 115, 0);
		printResultEntity(entityList);
	}

	@Test
	void testGetCostScenaio2() {
		System.out.println("Scenario-2 :");
		List<CpuAllocatorResultEntity> entityList = cpuMain.getCosts(8, 0, 29);
		printResultEntity(entityList);
	}
	
	@Test
	void testGetCostScenaio3() {
		System.out.println("Scenario-3 :");
		List<CpuAllocatorResultEntity> entityList = cpuMain.getCosts(7, 214, 95);
		printResultEntity(entityList);
	}
	
	private void printResultEntity(List<CpuAllocatorResultEntity> entityList) {
		entityList.stream().forEach(entity -> {
			System.out.println("Region : "+entity.getRegion());
			System.out.println("Total Cost : "+entity.getTotalCost());
			System.out.println("Servers : "+entity.getServers());
			System.out.println();
		});
	}

}
