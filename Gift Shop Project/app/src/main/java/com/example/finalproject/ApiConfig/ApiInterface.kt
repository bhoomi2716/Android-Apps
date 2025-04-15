package com.example.finalproject.ApiConfig

import com.example.finalproject.Model.CartModel
import com.example.finalproject.Model.CategoryModel
import com.example.finalproject.Model.DashboardModel
import com.example.finalproject.Model.SignInModel
import com.example.finalproject.Model.WishlistModel
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
    //Create Function For SignUp With Fields Name
    @FormUrlEncoded
    @POST("user_signup.php")
    fun SignUp(
        @Field("user_first_name") user_first_name : String,
        @Field("user_last_name") user_last_name : String,
        @Field("user_phone") user_phone : String,
        @Field("user_gender") user_gender : String,
        @Field("user_email") user_email : String,
        @Field("user_password") user_password : String,
        @Field("type") type : String
    ) : Call<Void>

    //Create Function For SignIn With Fields Name
    @FormUrlEncoded
    @POST("user_login.php")
    fun SignIn(
        @Field("user_phone") user_phone : String,
        @Field("user_password") user_password : String
    ) : Call<SignInModel>

    @GET("dashboard_view.php")
    fun dashboardViewData() : Call<List<DashboardModel>>

    @FormUrlEncoded
    @POST("category_view.php")
    fun categoryItemViewData(
        @Field("data") data: Int,
    ): Call<List<CategoryModel>>

    @FormUrlEncoded
    @POST("add_data_to_wishlist.php")
    fun addItemsWishlist(
        @Field("gift_name") gift_name : String?,
        @Field("gift_description") gift_description:String?,
        @Field("gift_price") gift_price:String?,
        @Field("image") image:String?,
        @Field("mobile") mobile:String?,
    ): Call<Void>

    @FormUrlEncoded
    @POST("viewwishlist.php")
    fun viewItemsWishlist(
        @Field("mobile") data: String?,
    ): Call<List<WishlistModel>>

    @FormUrlEncoded
    @POST("deletewishlist.php")
    fun deleteItemWishlist(
        @Field("id") id: Int?,
    ): Call<Void>

    @FormUrlEncoded
    @POST("add_data_to_cart.php")
    fun addItemCart(
        @Field("gift_name") gift_name:String?,
        @Field("gift_description") gift_description:String?,
        @Field("gift_price") gift_price : String?,
        @Field("image") image : String?,
        @Field("mobile") mobile : String?,
    ): Call<Void>

    @FormUrlEncoded
    @POST("viewcart.php")
    fun viewItemCart(
        @Field("mobile") data : String?,
    ): Call<List<CartModel>>

    @FormUrlEncoded
    @POST("deletecart.php")
    fun deleteItemCart(
        @Field("id") id : Int?,
    ): Call<Void>

    @FormUrlEncoded
    @POST("paymentadd.php")
    fun proceedPayment(
        @Field("pname") pname : String,
        @Field("pprice") pprice : String,
        @Field("pdes") pdes : String,
        @Field("pimage") pimage : String,
        @Field("mobile") mobile : String
    ): Call<Void>

    @Multipart
    @POST("addcategory.php")
    suspend fun AddCategory(
        @Part url: MultipartBody.Part,
        @Part("name") emp_name: RequestBody?,
    ): ResponseBody
}