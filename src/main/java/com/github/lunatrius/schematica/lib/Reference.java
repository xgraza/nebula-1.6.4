package com.github.lunatrius.schematica.lib;

import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Reference {
	public static final String NAME = "Schematica";

	public static final Logger logger = LogManager.getLogger("Schematica");
	public static Config config = null;
	public static File schematicDirectory = new File(Schematica.getDataDirectory(), "schematics");
}
