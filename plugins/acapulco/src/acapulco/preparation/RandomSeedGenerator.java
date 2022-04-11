/*
 * Author : Christopher Henard (christopher.henard@uni.lu)
 * Date : 01/03/14
 * Copyright 2013 University of Luxembourg – Interdisciplinary Centre for Security Reliability and Trust (SnT)
 * All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package acapulco.preparation;

import java.io.File;
import java.io.FileInputStream;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.Solver;
import org.sat4j.minisat.orders.PositiveLiteralSelectionStrategy;
import org.sat4j.minisat.orders.RandomWalkDecorator;
import org.sat4j.minisat.orders.VarOrderHeap;
import org.sat4j.reader.DimacsReader;
import org.sat4j.specs.ISolver;
import org.sat4j.tools.ModelIterator;

import acapulco.utils.FileUtils;

/**
 *
 * @author chris
 */
public class RandomSeedGenerator {

	private static final int SATtimeout = 1000;
	private static final long iteratorTimeout = 150000;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		String name, path;
		name = args[0];
		path = args[1];

		String inFileName = path + name + ".dimacs";
		String outFileName = path + name + ".dimacs.richseed";
		System.out.println(inFileName);

		createRandomSeed(inFileName, outFileName);
	}

	public static void createRandomSeed(String inputDimacsFMPath, String outputSeedPath) {
		try {
			IOrder order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);

			ISolver dimacsSolver = SolverFactory.instance().newMiniSATHeap();
			dimacsSolver.setTimeout(SATtimeout);
			DimacsReader dr = new DimacsReader(dimacsSolver);
			dr.parseInstance(new FileInputStream(inputDimacsFMPath));

			((Solver) dimacsSolver).setOrder(order);

			ISolver solverIterator = new ModelIterator(dimacsSolver);
			solverIterator.setTimeoutMs(iteratorTimeout);

			StringBuilder sb = new StringBuilder();

			if (solverIterator.isSatisfiable()) {
				int[] i = solverIterator.model();
				for (int j = 0; j < i.length; j++) {
					sb.append(i[j] + " \n");

				}
				sb.append("");
			} else {
				sb.append("no model");
			}

			FileUtils.writeStringToFile(new File(outputSeedPath), sb.toString());

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}