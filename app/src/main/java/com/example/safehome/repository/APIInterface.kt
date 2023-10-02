package com.example.safehome.repository

import com.example.safehome.constants.AppConstants
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.AllCommunities
import com.example.safehome.model.AllFacilitiesModel
import com.example.safehome.model.Blocks
import com.example.safehome.model.CategoryModel
import com.example.safehome.model.CommunityCities
import com.example.safehome.model.CommunityComplaintsModel
import com.example.safehome.model.CommunityStates
import com.example.safehome.model.DailyHelpBookingListModel
import com.example.safehome.model.DailyHelpHistoryModel
import com.example.safehome.model.DailyHelpRoles
import com.example.safehome.model.DailyHelpStaffModel
import com.example.safehome.model.DeleteFacilityModel
import com.example.safehome.model.FaciBookings
import com.example.safehome.model.FamilyDetail
import com.example.safehome.model.Flats
import com.example.safehome.model.GetAllHistoryServiceTypes
import com.example.safehome.model.GetAllNoticeStatus
import com.example.safehome.model.LoginDetialsData
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.MeetingCompletedModel
import com.example.safehome.model.MeetingUpcomingModel
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.PersonalComplaintsModel
import com.example.safehome.model.ServiceDataList
import com.example.safehome.model.ServiceProvidedTypesList
import com.example.safehome.model.ServiceTypesList
import com.example.safehome.model.ServicesBookingsList
import com.example.safehome.model.ServicesStaffHistoryList
import com.example.safehome.model.StaffModel
import com.example.safehome.model.UpdateMaintenanceModel
import com.example.safehome.model.TenantDetails
import com.example.safehome.model.TotalDuesMaintenanceList
import com.example.safehome.model.UpcomingEventsModel
import com.example.safehome.model.UserDetail
import com.example.safehome.model.VehicleDetails
import com.example.safehome.model.VehicleModel
import com.example.safehome.model.YearModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIInterface {

    @POST(AppConstants.RetrofitApis.mobileSignUp)
    fun mobileSignUp(
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @POST(AppConstants.RetrofitApis.resendSingUpOTP)
    fun resendSingUpOTP(
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @POST(AppConstants.RetrofitApis.verifySignUpOTP)
    fun verifySignUpOTP(
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @POST(AppConstants.RetrofitApis.mobileLogin)
    fun mobileLogin(
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @GET(AppConstants.RetrofitApis.communityStates)
    fun getCommunityStates(): Call<CommunityStates>


    @GET(AppConstants.RetrofitApis.communityCities)
    fun getCommunityCities(
        @Query("stateId") state: Int
    ): Call<CommunityCities>

    @GET(AppConstants.RetrofitApis.allCommunities)
    fun getAllCommunities(
        @Query("cityId") state: Int
    ): Call<AllCommunities>

    @GET(AppConstants.RetrofitApis.getBlocks)
    fun getBlocks(
        @Query("communityId") communityId: Int
    ): Call<Blocks>

    @GET(AppConstants.RetrofitApis.getFlats)
    fun getFlats(
        @Query("communityId") communityId: Int,
        @Query("BlockId") blockId: Int
    ): Call<Flats>


    @PUT(AppConstants.RetrofitApis.updateUserDetails)
    fun updateUserDetails(
        @Body jsonObject: JsonObject
    ): Call<UserDetail>

    @POST(AppConstants.RetrofitApis.verifyLoginOTP)
    fun verifyLoginOTP(
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @GET(AppConstants.RetrofitApis.getLoginUserDetails)
    fun getLoginUserDetails(
        @Header("Authorization") authorizationValue: String,
        @Query("userId") UserId: Int
    ): Call<LoginDetialsData>

    @POST(AppConstants.RetrofitApis.resendLoginOTP)
    fun resendLoginOTP(
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @GET(AppConstants.RetrofitApis.getFamilyMemberDetails)
    fun getFamilyMemberDetails(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") UserId: Int
    ): Call<FamilyDetail>

    @Multipart
    @POST(AppConstants.RetrofitApis.addMyFamily)
    fun addMyFamily(
        @Header("Authorization") authorizationValue: String,
        @Part("ResidentId") UserId: Int,
        @Part("FirstName") FirstName: String,
        @Part("LastName") LastName: String,
        @Part("MobileNo") MobileNo: String,
        @Part("Email") Email: String,
        @Part("AgeGroup") AgeGroup: String,
        @Part Image: MultipartBody.Part,
    ): Call<MobileSignUp>

    @Multipart
    @POST(AppConstants.RetrofitApis.addMyFamily)
    fun addMyFamilyNoImae(
        @Header("Authorization") authorizationValue: String,
        @Part("ResidentId") UserId: Int,
        @Part("FirstName") FirstName: String,
        @Part("LastName") LastName: String,
        @Part("MobileNo") MobileNo: String,
        @Part("Email") Email: String,
        @Part("AgeGroup") AgeGroup: String
    ): Call<MobileSignUp>

    @PUT(AppConstants.RetrofitApis.updateMyFamily)
    fun updateMyFamily(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @DELETE(AppConstants.RetrofitApis.deleteFamilyMember)
    fun deleteFamilyMember(
        @Header("Authorization") authorizationValue: String,
        @Query("Id") id: Int
    ): Call<MobileSignUp>

    @POST(AppConstants.RetrofitApis.addMyVehicle)
    fun addMyVehicle(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @GET(AppConstants.RetrofitApis.getAllVehiclesbyUserId)
    fun getAllVehiclesByUserId(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") UserId: Int
    ): Call<VehicleDetails>

    @PUT(AppConstants.RetrofitApis.updateMyVehicle)
    fun updateMyVehicle(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<MobileSignUp>

    @DELETE(AppConstants.RetrofitApis.deleteVehicle)
    fun deleteVehicle(
        @Header("Authorization") authorizationValue: String,
        @Query("VehicleID") id: Int
    ): Call<MobileSignUp>


    @GET(AppConstants.RetrofitApis.getAllTenants)
    fun getAllTenants(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") UserId: Int
    ): Call<TenantDetails>

    @Multipart
    @POST(AppConstants.RetrofitApis.addTenant)
    fun addTenant(
        @Header("Authorization") authorizationValue: String,
        @Part("ResidentId") UserId: Int,
        @Part("FirstName") FirstName: String,
        @Part("LastName") LastName: String,
        @Part("MobileNo") MobileNo: String,
        @Part("Email") Email: String,
        @Part imagePath: MultipartBody.Part,
    ): Call<MobileSignUp>

    @Multipart
    @POST(AppConstants.RetrofitApis.addTenant)
    fun addTenantNoImage(
        @Header("Authorization") authorizationValue: String,
        @Part("ResidentId") UserId: Int,
        @Part("FirstName") FirstName: String,
        @Part("LastName") LastName: String,
        @Part("MobileNo") MobileNo: Long,
        @Part("Email") Email: String,
    ): Call<MobileSignUp>

    @Multipart
    @PUT(AppConstants.RetrofitApis.updateTenant)
    fun updateTenant(
        @Header("Authorization") authorizationValue: String,
        @Part("TenantsId") TenantsId: Int,
        @Part("ResidentId") UserId: Int,
        @Part("FirstName") FirstName: String,
        @Part("LastName") LastName: String,
        @Part("MobileNo") MobileNo: Long,
        @Part("Email") Email: String,
        @Part imagePath: MultipartBody.Part,
    ): Call<MobileSignUp>

    @Multipart
    @PUT(AppConstants.RetrofitApis.updateTenant)
    fun updateTenantNoImage(
        @Header("Authorization") authorizationValue: String,
        @Part("TenantsId") TenantsId: Int,
        @Part("ResidentId") UserId: Int,
        @Part("FirstName") FirstName: String,
        @Part("LastName") LastName: String,
        @Part("MobileNo") MobileNo: Long,
        @Part("Email") Email: String,
    ): Call<MobileSignUp>

    @DELETE(AppConstants.RetrofitApis.deleteTenant)
    fun deleteTenant(
        @Header("Authorization") authorizationValue: String,
        @Query("TenantId") TenantsId: Int
    ): Call<MobileSignUp>

    @GET(AppConstants.RetrofitApis.getAllVehicleModels)
    fun getAllVehicleModels(
        @Header("Authorization") authorizationValue: String,
        @Query("VehicleType") UserId: Int
    ): Call<VehicleModel>

    @GET(AppConstants.RetrofitApis.getDueMaintenanceDetailsByResident)
    fun getDueMaintenanceDetailsByResident(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") ResidentId: Int
    ): Call<TotalDuesMaintenanceList>

    @GET(AppConstants.RetrofitApis.getPaidMaintenanaceDetailsByResident)
    fun GetPaidMaintenanaceDetailsByResident(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") ResidentId: String,
        @Query("year") year: String,
        @Query("categoryId") categoryId: Int
    )

    @Multipart
    @PUT(AppConstants.RetrofitApis.UpdateMaintenancePayment)
    fun UpdateMaintenancePayment(
        @Header("Authorization") authorizationValue: String,
        @Part("MaintenanceType") MaintenanceType: String,
        @Part("TransactionId") TransactionId: Int,
        @Part("ResidnetId") ResidnetId: Int,
        @Part("TotalAmount") TotalAmount: Int,
        @Part("PaymentMode") PaymentMode: String,
        @Part("TransactionStatus") TransactionStatus: String,
        @Part("TransactionNumber") TransactionNumber: String,
        @Part("TransactionPath") TransactionPath: String
    ): Call<UpdateMaintenanceModel>

    @GET(AppConstants.RetrofitApis.getAllMaintenanceCategorys)
    fun getAllMaintenanceCategorys(
        @Header("Authorization") authorizationValue: String
    ): Call<CategoryModel>

    @GET(AppConstants.RetrofitApis.getPaidMaintenanaceDetailsByResident)
    fun getPaidMaintenanaceDetailsByResident(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") ResidentId: Int,
        @Query("year") year: String? = null,
        @Query("categoryId") categoryId: String? = null
    ): Call<MaintenanceHistoryModel>

    @GET(AppConstants.RetrofitApis.getAllUpcomingEvents)
    fun getAllUpcomingEvents(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int,
        @Header("pageNumber") pageNumber: Int,
        @Header("residentId") residentId: String? = null
    ): Call<UpcomingEventsModel>

    @GET(AppConstants.RetrofitApis.getAllcompletedEvents)
    fun getAllcompletedEvents(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int,
        @Header("pageNumber") pageNumber: Int,
        @Header("year") year: String? = null,
        @Header("residentId") residentId: String? = null
    ): Call<UpcomingEventsModel>

    @GET(AppConstants.RetrofitApis.getAllFacilities)
    fun getAllFacilities(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int? = 10,
        @Header("pageNumber") pageNumber: Int? = 1
    ): Call<AllFacilitiesModel>

    @GET(AppConstants.RetrofitApis.getAllBookedFacilities)
    fun getAllBookedFacilities(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int? = 50,
        @Header("pageNumber") pageNumber: Int? = 1
    ): Call<FaciBookings>

    @Multipart
    @POST(AppConstants.RetrofitApis.bookFacility)
    fun BookFacility(
        @Header("Authorization") authorizationValue: String,
        @Part("ResidentId") ResidentId: Int,
        @Part("facilityID") facilityID: Int,
        @Part("Purpose") Purpose: String,
        @Part("NoOfDays") NoOfDays: Int,
        @Part("StartDate") StartDate: String,
        @Part("EndDate") EndDate: String,
        @Part("NoOfHours") NoOfHours: Int,
        @Part("StartTime") StartTime: String,
        @Part("EndTime") EndTime: String,
        @Part("Comments") Comments: String
    ): Call<AddServiceBookingList>


    @GET(AppConstants.RetrofitApis.getDailyHelpStaff)
    fun getDailyHelpList(
        @Header("Authorization") authorizationValue: String
    ): Call<DailyHelpRoles>


    @GET(AppConstants.RetrofitApis.getDailyHelp)
    fun getDailyHelpStaffList(
        @Header("Authorization") authorizationValue: String,
        @Header("staffTypeId") staffTypeId: String,
        @Header("availableOn") availableOn: String,
        @Header("availableTime") availableTime: String
    ): Call<DailyHelpStaffModel>


    @GET(AppConstants.RetrofitApis.getDailyHelpBookingList)
    fun getDailyHelpBookingListList(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") ResidentId: String
    ): Call<DailyHelpBookingListModel>

    @POST(AppConstants.RetrofitApis.addStaffBooking)
    fun addStaffBooking(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<AddServiceBookingList>

    @PUT(AppConstants.RetrofitApis.updateStaffBooking)
    fun updateStaffBooking(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<AddServiceBookingList>

    @DELETE(AppConstants.RetrofitApis.deleteBookStaff)
    fun deleteBookStaff(
        @Header("Authorization") authorizationValue: String,
        @Query("StaffBookingId") StaffBookingId: Int
    ): Call<AddServiceBookingList>


    @GET(AppConstants.RetrofitApis.dailyHelpHistory)
    fun getDailyHelpHistory(
        @Header("Authorization") authorizationValue: String,
        @Header("staffTypeId") staffTypeId: Int? = null,
        @Header("year") year: String? = null
    ): Call<DailyHelpHistoryModel>


    @GET(AppConstants.RetrofitApis.yearList)
    fun yearList(
        @Header("Authorization") authorizationValue: String,
    ): Call<YearModel>

    @GET(AppConstants.RetrofitApis.staffList)
    fun staffList(
        @Header("Authorization") authorizationValue: String,
    ): Call<StaffModel>

    @GET(AppConstants.RetrofitApis.GetServiceTypeswithServiceCount)
    fun GetServiceTypeswithServiceCount(
        @Header("Authorization") authorizationValue: String
    ): Call<ServiceTypesList>

    @GET(AppConstants.RetrofitApis.GetServiceDataByServiceType)
    fun GetServiceDataByServiceType(
        @Header("Authorization") authorizationValue: String,
        @Header("serviceTypeId") serviceTypeId: String,
        @Header("serviceProvideId") serviceProvideId: String?= "",
        @Header("availableOn") availableOn: String?= "",
    ): Call<ServiceDataList>

    @GET(AppConstants.RetrofitApis.getServiceProvidedByServiceType)
    fun getServiceProvidedByServiceType(
    @Header("Authorization") authorizationValue: String,
    @Query("ServiceTypeId") ServiceTypeId: Int
    ): Call<ServiceProvidedTypesList>

    @POST(AppConstants.RetrofitApis.addServiceBooking)
    fun AddServiceBooking(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<AddServiceBookingList>

    @PUT(AppConstants.RetrofitApis.updateServiceBooking)
    fun updateServiceBooking(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<AddServiceBookingList>

    @DELETE(AppConstants.RetrofitApis.deleteServiceBookingById)
    fun deleteServiceBookingById(
        @Header("Authorization") authorizationValue: String,
        @Query("serviceBookingId") serviceBookingId: Int
    ): Call<AddServiceBookingList>

    @GET(AppConstants.RetrofitApis.getAllBookedServiceByResidentId)
    fun getAllBookedServiceByResidentId(
        @Header("Authorization") authorizationValue: String,
        @Query("ResidentId") ResidentId: Int
    ): Call<ServicesBookingsList>

    @GET(AppConstants.RetrofitApis.getStaffPaymentHistory)
    fun getStaffPaymentHistory(
        @Header("Authorization") authorizationValue: String,
        @Header("staffTypeId") staffTypeId: String? = "",
        @Header("year") year: String? = "",
        @Header("residentId") residentId: String? = ""

    ): Call<ServicesStaffHistoryList>

    @GET(AppConstants.RetrofitApis.getAllServiceTypes)
    fun getAllServiceTypes(
        @Header("Authorization") authorizationValue: String
    ): Call<GetAllHistoryServiceTypes>

    @DELETE(AppConstants.RetrofitApis.deleteFacility)
    fun deleteFacility(
        @Header("Authorization") authorizationValue: String,
        @Query("bookFacilityId") bookFacilityId: Int

    ): Call<DeleteFacilityModel>

    @Multipart
    @PUT(AppConstants.RetrofitApis.updateServiceBookingPayment)
    fun updateServiceBookingPayment(
        @Header("Authorization") authorizationValue: String,
        @Part("ServiceBookingId") ServiceBookingId: Int,
        @Part("ResidnetId") ResidnetId: Int,
        @Part("PayType") PayType: String,
        @Part("PaymentMode") PaymentMode: String,
        @Part("TransactionStatus") TransactionStatus: String,
        @Part("TransactionNumber") TransactionNumber: String,
        @Part("TransactionPath") TransactionPath: String,
        @Part("TransactionComments") TransactionComments: String
        ): Call<AddServiceBookingList>

    @GET(AppConstants.RetrofitApis.getallNoticesStatus)
    fun getallNoticesStatus(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int,
        @Header("pageNumber") pageNumber: Int,
        @Query("Noticeview") Noticeview: Boolean? = false,
        @Query("year") year: String? = null
    ): Call<GetAllNoticeStatus>

    @PUT(AppConstants.RetrofitApis.updateNoticeRead)
    fun updateNoticeRead(
        @Header("Authorization") authorizationValue: String,
        @Query("readStatusId") readStatusId:Int
    ): Call<JsonObject>


    @GET(AppConstants.RetrofitApis.getPersonalComplaints)
    fun getPersonalComplaints(
        @Header("Authorization") authorizationValue: String,
        @Query("complaintStatus") complaintStatus: String? = null,
    @Query("year") year: String? = null
    ): Call<PersonalComplaintsModel>

    @GET(AppConstants.RetrofitApis.getPersonalComplaints)
    fun getCommunityComplaints(
        @Header("Authorization") authorizationValue: String,
        @Query("complaintStatus") complaintStatus: String? = null,
    @Query("year") year: String? = null
    ): Call<CommunityComplaintsModel>

    @GET(AppConstants.RetrofitApis.getPersonalComplaints)
    fun getMeetingUpcoming(
        @Header("Authorization") authorizationValue: String,
        @Query("complaintStatus") complaintStatus: String? = null,
    @Query("year") year: String? = null
    ): Call<MeetingUpcomingModel>

    @GET(AppConstants.RetrofitApis.getPersonalComplaints)
    fun getMeetingCompleted(
        @Header("Authorization") authorizationValue: String,
        @Query("complaintStatus") complaintStatus: String? = null,
    @Query("year") year: String? = null
    ): Call<MeetingCompletedModel>



}