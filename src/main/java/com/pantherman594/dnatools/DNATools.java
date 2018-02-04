package com.pantherman594.dnatools;

import java.util.*;

/**
 * Created by david on 4/03.
 *
 * @author david
 */
public class DNATools {

    private static Map<String, AminoAcids> codons = new HashMap<>();
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        // Put all amino acids in the map as codon:acid
        for (AminoAcids aminoAcids : AminoAcids.values()) {
            for (String base : aminoAcids.getCodons()) {
                codons.put(base, aminoAcids);
            }
        }

        start();
    }

    private static void clear() {
        StringBuilder clr = new StringBuilder();
        for (int i = 0; i < 50; i++) clr.append("\n");
        System.out.println(clr);
    }

    private static void start() {
        System.out.println("1: Transcribe DNA <-> RNA");
        System.out.println("2: Translate DNA/RNA -> Amino acid sequence");
        System.out.println("3: Validate two DNA strands");
        System.out.print("Enter a number (1-3): ");

        String funcS = input.nextLine();
        int func = 0;
        try {
            func = Integer.parseInt(funcS);
        } catch (NumberFormatException ignored) {}
        System.out.println();

        switch(func) {
            case 1:
                System.out.print("Enter a DNA or RNA sequence: ");
                String seq = input.nextLine().toUpperCase();

                // Validation
                if (!isValidSeq(seq)) {
                    System.out.println("Invalid DNA/RNA sequence.");
                    break;
                }

                System.out.println();
                transcribe(seq);
                break;
            case 2:
                System.out.print("Enter a DNA or RNA sequence: ");
                seq = input.nextLine().toUpperCase();

                // Validation
                if (!isValidSeq(seq) || seq.length() % 3 != 0) { // Make sure the sequence is valid, and has a multiple of 3 codons
                    System.out.println("\nInvalid DNA/RNA sequence.");
                    break;
                }

                // Asks if user wants the full names of the amino acids. If user inputs Y/y, will show full names. Otherwise, will not.
                System.out.println("Show full names?");
                System.out.print("Y/n (n): ");
                boolean full = input.nextLine().equalsIgnoreCase("Y");

                System.out.println();
                translate(seq, full);
                break;
            case 3:
                System.out.print("Enter DNA sequence 1: ");
                String seq1 = input.nextLine().toUpperCase();
                System.out.print("Enter DNA sequence 2: ");
                String seq2 = input.nextLine().toUpperCase();
                System.out.println();

                // Validation
                if (seq1.length() != seq2.length()) {
                    System.out.println("Sequences are not of equal length.");
                    break;
                }
                if (!isValidSeq(seq1 + seq2) || (seq1 + seq2).contains("U")) { // Make sure the sequences are valid DNA sequences
                    System.out.println("One of the DNA sequences are invalid.");
                    break;
                }

                validate(seq1, seq2);
                break;
            default:
                System.out.println("Invalid input.");
                break;
        }

        // Asks if user wants to restart. If user inputs N/n, will quit. Otherwise, will restart.
        System.out.println("\nRestart?");
        System.out.print("Y/n (y): ");
        boolean restart = !input.nextLine().equalsIgnoreCase("N");
        if (restart) {
            clear();
            start();
        }
    }

    private static boolean isValidSeq(String seq) {
        // Ensures the string contains only the characters GCAT (DNA) or GCAU (RNA)
        return seq != null && !seq.isEmpty() && (seq.replaceAll("[GCAT]+", "").isEmpty() ||
                seq.replaceAll("[GCAU]+", "").isEmpty());
    }

    private static String toRna(String seq) {
        if (!isValidSeq(seq)) return null;
        return seq.replace("T", "U"); // Return the string with U's instead of T's
    }

    private static String toDna(String seq) {
        if (!isValidSeq(seq)) return null;
        return seq.replace("U", "T"); // Return the string with T's instead of U's
    }

    private static char flip(char toFlip) {
        switch(toFlip) {
            case 'T':
                return 'A';
            case 'C':
                return 'G';
            case 'A':
                return 'T';
            case 'G':
                return 'C';
            default:
                return 'X';
        }
    }

    private static void transcribe(String seq) {
        if (seq.contains("U")) { // RNA sequence
            System.out.println("\t" + toDna(seq));
        } else { // Can be either DNA or RNA, but without U's it doesn't matter if it's RNA. Cannot have both U's and T's, as ensured above
            System.out.println("\t" + toRna(seq));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void translate(String seq, boolean fullName) {
        if (seq.contains("U")) { // RNA sequence
            seq = toDna(seq);
        }

        StringBuilder acidSeq = new StringBuilder();
        for (int i = 0; i < seq.length(); i += 3) {
            acidSeq.append(" - ");
            AminoAcids acid = codons.get(seq.substring(i, i + 3));
            if (acid == AminoAcids.MET && (i == 0 || codons.get(seq.substring(i - 3, i)) == AminoAcids.STOP)) acidSeq.append("Start"); // Print as "Start" if Met in beginning or after Stop
            else {
                if (fullName) acidSeq.append(acid.getFullName());
                else acidSeq.append(acid.getName());
            }
        }
        System.out.println("\t" + acidSeq.toString().substring(3));
    }

    private static void validate(String seq1, String seq2) {
        boolean isValid = true;
        StringBuilder discrepancies = new StringBuilder("\t");

        for (int i = 0; i < seq1.length(); i++) {
            if (seq1.charAt(i) != flip(seq2.charAt(i))) {
                isValid = false;
                discrepancies.append("X");
            } else {
                discrepancies.append(" ");
            }
        }

        if (isValid) {
            System.out.println("\tStrand is valid");
        } else {
            System.out.println("\t" + seq1);
            System.out.println(discrepancies.toString());
            System.out.println("\t" + seq2);
        }
    }

    @SuppressWarnings("unused")
    private enum AminoAcids { // Created with reference to R. Akeson's "DNA Dictionary"
        PHE("Phenylalanine", "TTT", "TTC"),
        LEU("Leucine", "CTT", "CTC", "CTA", "CTG"),
        ILE("Isoleucine", "ATT", "ATC", "ATA"),
        MET("Methionine", "ATG"), // also the start codon
        VAL("Valine", "GTT", "GTC", "GTA", "GTG"),
        SER("Serine", "TCT", "TCC", "TCA", "TCG", "AGT", "AGC"),
        PRO("Proline", "CCT", "CCC", "CCA", "CCG"),
        THR("Threonine", "ACT", "ACC", "ACA", "ACG"),
        ALA("Alanine", "GCT", "GCC", "GCA", "GCG"),
        TYR("Tyrosine", "TAT", "TAC"),
        HIS("Histidine", "CAT", "CAC"),
        GLN("Glutamine", "CAA", "CAG"),
        ASN("Asparagine", "AAT", "AAC"),
        LYS("Lysine", "AAA", "AAG"),
        ASP("Aspartic acid", "GAT", "GAC"),
        GLU("Glutamic acid", "GAA", "GAG"),
        CYS("Cysteine", "TGT", "TGC"),
        TRP("Tryptophan", "TGG"),
        ARG("Arginine", "CGT", "CGC", "CGA", "CGG", "AGA", "AGG"),
        GLY("Glycine", "GGT", "GGC", "GGA", "GGG"),
        STOP("Stop", "TAA", "TAG", "TGA");

        private String fullName;
        private List<String> codons;

        AminoAcids(String fullName, String... codons) {
            this.fullName = fullName;
            this.codons = new ArrayList<>();
            Collections.addAll(this.codons, codons);
        }

        public String getName() {
            return this.name().substring(0, 1) + this.name().toLowerCase().substring(1);
        }

        public String getFullName() {
            return fullName;
        }

        public List<String> getCodons() {
            return codons;
        }
    }
}
