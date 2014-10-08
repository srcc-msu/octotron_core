package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.reactions.PreparedResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReactionInvoker implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final AttributeList<IModelAttribute> attributes;

	public ReactionInvoker(AttributeList<IModelAttribute> attributes)
	{
		this.attributes = attributes;
	}

	public void run()
	{
		long reactions_count = 0;

		for(IModelAttribute attribute : attributes)
		{
			for (PreparedResponse response : attribute.ProcessReactions())
			{

				if(!ExecutionController.Get().IsSilent())
					ExecutionController.Get().AddResponse(response);

				reactions_count++;
			}
		}

		if(ExecutionController.Get().IsSilent() && reactions_count > 0)
			LOGGER.log(Level.INFO, "silent mode, reactions ignored: " + reactions_count);
	}
}
