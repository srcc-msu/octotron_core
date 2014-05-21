package ru.parallel.octotron;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import ru.parallel.octotron.core.*;
import ru.parallel.octotron.neo4j.impl.Neo4jGraphTest;
import ru.parallel.octotron.utils.ObjectListTest;
import ru.parallel.octotron.utils.OctoAttributeListTest;

@RunWith(Suite.class)
@SuiteClasses(
{
	GraphServiceTest.class,
	IndexTest.class,
	OctoAttributeTest.class,
	OctoEntityTest.class,
	Neo4jGraphTest.class,
	ObjectListTest.class,
	OctoAttributeListTest.class
})
public abstract class AllTests {}
