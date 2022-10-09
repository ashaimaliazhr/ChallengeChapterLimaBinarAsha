package asha.binar.challengechapterlimabinarasha.model.credit

import com.google.gson.annotations.SerializedName

data class GetCreditResponse (
    @SerializedName("cast")
    val cast: List<Cast>,
    @SerializedName("crew")
    val crew: List<Crew>,
    @SerializedName("id")
    val id: Int
    )