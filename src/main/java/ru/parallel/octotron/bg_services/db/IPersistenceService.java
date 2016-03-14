/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.bg_services.db;

import ru.parallel.octotron.persistence.IPersistenceManager;

public interface IPersistenceService extends IPersistenceManager
{
	void Operate();
	void Finish();
	void Clean();
	void Check();
}
