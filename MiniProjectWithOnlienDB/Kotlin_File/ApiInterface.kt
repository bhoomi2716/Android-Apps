package com.example.projectsignupandsigninandcrud

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface
{
    @FormUrlEncoded
    @POST("signup.php")
    fun SignUp(
        @Field("name") name : String,
        @Field("surname") surname : String,
        @Field("email") email : String,
        @Field("gender") gender : String,
        @Field("mobile") mobile : String,
        @Field("password") password : String

    ) : Call<Void>

    @FormUrlEncoded
    @POST("signin.php")
    fun SignIn(

        @Field("email") email : String,
        @Field("password") password : String

    ) : Call<Model>

    @Multipart
    @POST("productinsert.php")
    fun InsertProduct (
        @Part("pname") pname:RequestBody,
        @Part("pprice") pprice:RequestBody,
        @Part("pdesc") pdesc:RequestBody,
        @Part("pstatus") pstatus: RequestBody,
        @Part pimage: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("productview.php")
    fun ViweProduct() : Call<List<ProductModel>>

    @Multipart
    @POST("productupdate.php")
    fun UpdateProduct (
        @Part("pid") pid: Int,
        @Part("pname") pname:RequestBody,
        @Part("pprice") pprice:RequestBody,
        @Part("pdesc") pdesc:RequestBody,
        @Part("pstatus") pstatus: RequestBody,
        @Part pimage: MultipartBody.Part
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("productdelete.php")
    fun DeleteProduct(
        @Field("pid") pid:Int,
    ): Call<Void>
}