package life.sim.biology.interactions

import life.sim.biology.molecules.Dna
import life.sim.biology.molecules.MRna
import life.sim.biology.molecules.TRna

fun Dna.forwardBindingSurface(id: MoleculeId): BindingSurface =
    BindingSurface(id, BindingStrand.FORWARD, forward)

fun Dna.reverseBindingSurface(id: MoleculeId): BindingSurface =
    BindingSurface(id, BindingStrand.REVERSE, reverse)

fun MRna.bindingSurface(id: MoleculeId): BindingSurface =
    BindingSurface(id, BindingStrand.SINGLE, toNucleotideSequence())

fun TRna.bindingSurface(id: MoleculeId): BindingSurface =
    BindingSurface(id, BindingStrand.SINGLE, toNucleotideSequence())

