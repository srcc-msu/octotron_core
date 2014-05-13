package test.java;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
{
	TestNeo4jGraph.class
	, TestGraphService.class
	, TestAttrList.class
	, TestAutoList.class
	, TestGenerators.class
	, TestIndex.class
	, TestNetwork.class
	, TestNode.class
})
public abstract class AllTests {}
