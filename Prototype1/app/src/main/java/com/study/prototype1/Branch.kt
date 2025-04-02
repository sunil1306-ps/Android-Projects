package com.study.prototype1

class Branch {
    //val year = listOf("First Year", "Second Year", "Third Year")
    val branch = listOf("CSE", "IT", "CIVIL", "MECHANICAL", "ECE", "EEE", "CHEMICAL", "MBA")
    val cse_1 = listOf("cse_1_1", "cse_1_2", "cse_1_3")
    val cse_2 = listOf("cse_2_1", "cse_2_2", "cse_2_3")
    val cse_3 = listOf("cse_3_1", "cse_3_2", "cse_3_3")
    val it_1 = listOf("it_1_1", "it_1_2", "it_1_3")
    val it_2 = listOf("it_2_1", "it_2_2", "it_2_3")
    val it_3 = listOf("it_3_1", "it_3_2", "it_3")
    val civil_1 = listOf("civil_1_1", "civil_1_2", "civil_3_3")
    val civil_2 = listOf("civil_2_1", "civil_2_2", "civil_2_3")
    val civil_3 = listOf("civil_3_1", "civil_3_2", "civil_3_3")
    val mech_1 = listOf("mech_1_1", "mech_1_2", "mech_1_3")
    val mech_2 = listOf("mech_2_1", "mech_2_2", "mech_2_3")
    val mech_3 = listOf("mech_3_1", "mech_3_2", "mech_3_3")
    var year_ids = mapOf("First Year" to "1", "Second Year" to "2", "Third Year" to "3")
    var branch_ids = mapOf("CSE" to "1", "IT" to "2", "CIVIL" to "3", "MECHANICAL" to "4", )
}

val branch = Branch()