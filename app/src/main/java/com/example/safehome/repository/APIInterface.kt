package com.example.safehome.repository

import com.example.safehome.alert.AlertDeleteResponse
import com.example.safehome.alert.AlertRequest
import com.example.safehome.alert.AlertResponse
import com.example.safehome.alert.EmergencyTypeModel
import com.example.safehome.constants.AppConstants
import com.example.safehome.enews.UserENewsListModel
import com.example.safehome.forums.AddReplyCommentModel
import com.example.safehome.forums.AddUpdateForumResponse
import com.example.safehome.forums.GetAllForumCommentsModel
import com.example.safehome.forums.GetAllForumDetailsModel
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
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.EmergencyContactCategory
import com.example.safehome.model.FaciBookings
import com.example.safehome.model.FamilyDetail
import com.example.safehome.model.Flats
import com.example.safehome.model.GetAllAssociationMembersModel
import com.example.safehome.model.GetAllHistoryServiceTypes
import com.example.safehome.model.GetAllNoticeStatus
import com.example.safehome.model.GetAllPollDetailsModel
import com.example.safehome.model.GetPollResultModel
import com.example.safehome.model.LoginDetialsData
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.MeetingResponseStatusMaster
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.PersonalComplaintsModel
import com.example.safehome.model.PollsKeepDropdownModel
import com.example.safehome.model.PollsPostedDropDownModel
import com.example.safehome.model.RelationshipTypesModel
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
import com.example.safehome.model.UpcomingMeetingsModel
import com.example.safehome.model.UpdateAttendStatusMeetingResponse
import com.example.safehome.model.UserDetail
import com.example.safehome.model.VehicleDetails
import com.example.safehome.model.VehicleModel
import com.example.safehome.model.YearModel
import com.example.safehome.policies.PoliciesModel
import com.example.safehome.polls.AddPollResponse
import com.example.safehome.visitors.ApprovalStatusModel
import com.example.safehome.visitors.GetAllVisitorDetailsModel
import com.example.safehome.visitors.ScheduleModel
import com.example.safehome.visitors.ServiceProviderModel
import com.example.safehome.visitors.VisitorPostResponse
import com.example.safehome.visitors.VisitorsTypeDropdownModel
import com.example.safehome.visitors.staff.DeleteVisitorDetailsModel
import com.example.safehome.visitors.staff.StaffServiceBookedDropDownModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
        @Header("year") year: String? = null,
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

    @Multipart
    @PUT(AppConstants.RetrofitApis.updateBookFacility)
    fun updateBookFacility(
        @Header("Authorization") authorizationValue: String,
        @Part("BookFacilityId") BookFacilityId: Int,
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

    @Multipart
    @PUT(AppConstants.RetrofitApis.updatePaymentForBookFacility)
    fun updatePaymentForBookFacility(
        @Header("Authorization") authorizationValue: String,
        @Part("BookFacilityId") BookFacilityId: Int,
        @Part("TotalAmount") TotalAmount: Int,
        @Part("PaymentMode") PaymentMode: String,
        @Part("PayType") PayType: String,
        @Part("TransactionStatus") TransactionStatus: String,
        @Part("TransactionNumber") TransactionNumber: String,
        @Part("TransactionPath") TransactionPath: String,
        @Part("TransactionComments") TransactionComments: String
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

    @Multipart
    @PUT(AppConstants.RetrofitApis.updateStaffBookingPayment)
    fun updateStaffBookingPayment(
        @Header("Authorization") authorizationValue: String,
        @Part("StaffBookingId") StaffBookingId : Int,
        @Part("ResidnetId") ResidnetId : Int,
        @Part("PayType") PayType : String,
        @Part("PaymentMode") PaymentMode : String,
        @Part("TransactionStatus") TransactionStatus : String,
        @Part("TransactionNumber") TransactionNumber : String,
        @Part("TransactionPath") TransactionPath : String,
        @Part("TransactionComments") TransactionComments : String
    ): Call<AddServiceBookingList>


    @GET(AppConstants.RetrofitApis.dailyHelpHistory)
    fun getDailyHelpHistory(
        @Header("Authorization") authorizationValue: String,
        @Header("staffTypeId") staffTypeId: Int? = null,
        @Header("residentId") residentId : Int,
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
        @Query("Noticeview") Noticeview: String? = "false",
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

    @GET(AppConstants.RetrofitApis.getAllUpcomingMeetings)
    fun getMeetingUpcoming(
        @Header("Authorization") authorizationValue: String,
        @Header("year") year: String? = null,
        @Header("pageNumber") pageNumber: Int,
        @Header("resultsPerPage") resultsPerPage: String
    ): Call<UpcomingMeetingsModel>

    @GET(AppConstants.RetrofitApis.getAllCompletedMeetings)
    fun getMeetingCompleted(
        @Header("Authorization") authorizationValue: String,
        @Header("year") year: String? = null,
        @Header("pageNumber") pageNumber: Int,
        @Header("resultsPerPage") resultsPerPage: String
    ): Call<UpcomingMeetingsModel>

    @GET(AppConstants.RetrofitApis.getAllResponseStatusMaster)
    fun getAllResponseStatusMaster(
        @Header("Authorization") authorizationValue: String
    ): Call<MeetingResponseStatusMaster>

    @PUT(AppConstants.RetrofitApis.updateAttendingResponseStatus)
    fun updateAttendingResponseStatus(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ): Call<UpdateAttendStatusMeetingResponse>

    @GET(AppConstants.RetrofitApis.getAllPollDetails)
    fun getAllPollDetails(
        @Header("Authorization") authorizationValue: String,
        @Header("userId") userId: Int,
        @Header("year") year: String,
        @Header("pageNumber") pageNumber: Int,
        @Header("resultsPerPage") resultsPerPage: Int
        ):Call<GetAllPollDetailsModel>

    @GET(AppConstants.RetrofitApis.getPollResults)
    fun getPollResults(
        @Header("Authorization") authorizationValue: String,
        @Query("pollId") pollId: Int
        ): Call<GetPollResultModel>

    @FormUrlEncoded
    @POST(AppConstants.RetrofitApis.addPoll)
    fun addPoll(
        @Header("Authorization") authorizationValue: String,
        @FieldMap pollOptionsMap: MutableMap<String, Any>
    ): Call<AddPollResponse>

    @FormUrlEncoded
    @PUT(AppConstants.RetrofitApis.updatePoll)
    fun updatePoll(
        @Header("Authorization") authorizationValue: String,
        @FieldMap pollOptionsMap: MutableMap<String, Any>
    ): Call<AddPollResponse>

    @GET(AppConstants.RetrofitApis.getKeepForDropDown)
    fun getKeepForDropDown(
        @Header("Authorization") authorizationValue: String
    ): Call<PollsKeepDropdownModel>

    @GET(AppConstants.RetrofitApis.getAllPostedToDropDown)
    fun getAllPostedToDropDown(
        @Header("Authorization") authorizationValue: String
    ) : Call<PollsPostedDropDownModel>

    @DELETE(AppConstants.RetrofitApis.deletePollDetails)
    fun deletePollDetails(
        @Header("Authorization") authorizationValue: String,
        @Query("pollId") pollId : Int
    ): Call<AddServiceBookingList>



    @GET(AppConstants.RetrofitApis.getAllForumDetails)
    fun getAllForumDetails(
        @Header("Authorization") authorizationValue: String,
        @Header("startDate") postedTo: String,
        @Header("postedBy") postedBy: String,
        @Header("endDate") endDate: String,
        @Header("pageNumber") pageNumber: String,
        @Header("resultsPerPage") resultsPerPage: String
    ):Call<GetAllForumDetailsModel>

    @FormUrlEncoded
    @POST(AppConstants.RetrofitApis.addForum)
    fun addForum(
        @Header("Authorization") authorizationValue: String,
        @FieldMap forumOptionsMap: MutableMap<String, Any>
    ): Call<AddUpdateForumResponse>

    @FormUrlEncoded
    @PUT(AppConstants.RetrofitApis.updateForum)
    fun updateForum(
        @Header("Authorization") authorizationValue: String,
        @FieldMap forumOptionsMap: MutableMap<String, Any>
    ): Call<AddUpdateForumResponse>

    @DELETE(AppConstants.RetrofitApis.deleteForumDetails)
    fun deleteForumDetails(
        @Header("Authorization") authorizationValue: String,
        @Query("forumId") forumId : Int
    ): Call<AddUpdateForumResponse>

    @GET(AppConstants.RetrofitApis.getAllCommentDetailsByForumId)
    fun getAllCommentDetailsByForumId(
        @Header("Authorization") authorizationValue: String,
        @Query("forumId") forumId : Int
    ) : Call<GetAllForumCommentsModel>

    @POST(AppConstants.RetrofitApis.addForumCommentDetails)
    fun addForumCommentDetails(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
        ) : Call<AddServiceBookingList>

    @POST(AppConstants.RetrofitApis.addForumCommentReply)
    fun addForumCommentReply(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
        ): Call<AddReplyCommentModel>

    @DELETE(AppConstants.RetrofitApis.deleteForumCommentDetails)
    fun deleteForumCommentDetails(
        @Header("Authorization") authorizationValue: String,
        @Query("commentId") commentId: String
        ) : Call<AddServiceBookingList>

    @GET(AppConstants.RetrofitApis.getAllVisitorDetails)
    fun getAllVisitorDetails(
        @Header("Authorization") authorizationValue: String,
        @Header("visitorId") visitorId: String,
        @Header("visitorStatusId") visitorStatusId: String,
        @Header("VisitorTypeServiceCategoryId") VisitorTypeServiceCategoryId: String,
        @Header("visitorTypeId") visitorTypeId: String,
        @Header("invitedRoleId") invitedRoleId: String,
        @Header("blockId") blockId: String,
        @Header("flatId") flatId: String,
        @Header("residentId") residentId: String,
        @Header("startDate") startDate: String,
        @Header("endDate") endDate: String,
        @Header("pageNumber") pageNumber: String,
        @Header("resultsPerPage") resultsPerPage: String,
        @Header("allowFor") allowFor: String,
        ): Call<GetAllVisitorDetailsModel>

    @GET(AppConstants.RetrofitApis.getAllVisitorTypeDropdown)
    fun getAllVisitorTypeDropdown(
        @Header("Authorization") authorizationValue: String
        ): Call<VisitorsTypeDropdownModel>

    @GET(AppConstants.RetrofitApis.getServiceProviderDropdown)
    fun getServiceProviderCall(
        @Header("Authorization") authorizationValue: String,
        @Query("VisitorTypeId") commentId: Int
        ): Call<ServiceProviderModel>

    @GET(AppConstants.RetrofitApis.GetVisitorScheduleDropdown)
    fun getScheduleCall(
        @Header("Authorization") authorizationValue: String,
        @Query("VisitorTypeId") commentId: Int
        ): Call<ScheduleModel>

    @FormUrlEncoded
    @POST(AppConstants.RetrofitApis.postVisitorAPI)
    fun postVisitorAPICall(
        @Header("Authorization") authorizationValue: String,
        @FieldMap inviteVisitor: MutableMap<String, Any>
    ): Call<VisitorPostResponse>

    @FormUrlEncoded
    @PUT(AppConstants.RetrofitApis.updateVisitorAPI)
    fun updateVisitorAPI(
        @Header("Authorization") authorizationValue: String,
        @FieldMap inviteVisitor: MutableMap<String, Any>
    ): Call<VisitorPostResponse>


    @GET(AppConstants.RetrofitApis.getAllApprovalStatusTypes)
    fun getAllApprovalStatusTypes(
        @Header("Authorization") authorizationValue: String
    ): Call<ApprovalStatusModel>

    @DELETE(AppConstants.RetrofitApis.deleteVisitorDetails)
    fun deleteVisitorDetails(
        @Header("Authorization") authorizationValue: String,
        @Query("VisitorId") VisitorId: Int
    ): Call<DeleteVisitorDetailsModel>

    @GET(AppConstants.RetrofitApis.getStaffdetailsbyStafftypeIdDropdown)
    fun getStaffdetailsbyStafftypeIdDropdown(
        @Header("Authorization") authorizationValue: String,
        @Query("StaffTypeId") StaffTypeId: Int,
        @Query("AlreadybookedStatus") AlreadybookedStatus: String
        ): Call<StaffServiceBookedDropDownModel>

    @GET(AppConstants.RetrofitApis.getpoliciesAPI)
    fun getPoliciesAPI(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int ?= 50,
        @Header("pageNumber") pageNumber: Int ?= 1
    ): Call<PoliciesModel>


    @GET(AppConstants.RetrofitApis.getEmergentContactAPI)
    fun getEmergentContactAPI(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int ?= 50,
        @Header("pageNumber") pageNumber: Int ?= 1,
        @Header("contactTypeId") contactTypeId: Int ?= 0
    ): Call<EmergencyContact>

    @GET(AppConstants.RetrofitApis.getEmergentContactCategoryAPI)
    fun getEmergentContactCategoryAPI(
        @Header("Authorization") authorizationValue: String,
        @Header("resultsPerPage") resultsPerPage: Int ?= 50,
        @Header("pageNumber") pageNumber: Int ?= 1
    ): Call<EmergencyContactCategory>

    @GET(AppConstants.RetrofitApis.getAllUserENewsDetails)
    fun getAllUserENewsDetails(
        @Header("Authorization") authorizationValue: String,
    @Header("userId") userId: String,
    @Header("month") month: String,
    @Header("year") year: String,
    @Header("fromDate") fromDate: String,
    @Header("toDate") toDate: String,
    @Header("pageNumber") pageNumber: String,
    @Header("resultsPerPage") resultsPerPage: String,
    ): Call<UserENewsListModel>

    @PUT(AppConstants.RetrofitApis.updatePollVotingStatus)
    fun updatePollVotingStatus(
        @Header("Authorization") authorizationValue: String,
        @Body jsonObject: JsonObject
    ):Call<AddServiceBookingList>

    @GET(AppConstants.RetrofitApis.getAllAssociatonMembers)
    fun getAllAssociatonMembers(
        @Header("Authorization") authorizationValue: String,
      /*  @Header("clientRoleId") clientRoleId: Int,
        @Header("statusId") statusId: Int,
        @Header("flatId") flatId: Int,
        @Header("blockId") blockId: Int,
        @Header("fullName") fullName: String,
        @Header("startDate") startDate: String,
        @Header("endDate") endDate: String,*/
        @Header("pageNumber") pageNumber: Int,
        @Header("resultsPerPage") resultsPerPage: Int
        ) : Call<GetAllAssociationMembersModel>

    @GET(AppConstants.RetrofitApis.getAllRelationShipTypes)
    fun getAllRelationShipTypes(
        @Header("Authorization") authorizationValue: String
        ):Call<RelationshipTypesModel>

    @GET(AppConstants.RetrofitApis.getAlertUrl)
    fun getAllAlertDetails(
        @Header("Authorization") authorizationValue: String,
        @Header("pageNumber") pageNumber: String,
        @Header("resultsPerPage") resultsPerPage: String
    ):Call<AlertResponse>

    @DELETE(AppConstants.RetrofitApis.deleteAlertUrl)
    fun deleteAlert(
        @Header("Authorization") authorizationValue: String,
        @Query("alertId") alertId : Int
    ): Call<AlertDeleteResponse>

    @GET(AppConstants.RetrofitApis.GetAllEmergencyTypeDetailsDropDown)
    fun GetAllEmergencyTypeDetailsDropDown(
        @Header("Authorization") authorizationValue: String
    ): Call<EmergencyTypeModel>

    @POST(AppConstants.RetrofitApis.SendAlertDetails)
    fun SendAlertDetails(
        @Header("Authorization") authorizationValue: String,
        @Body alertRequest: AlertRequest
    ): Call<AlertDeleteResponse>


    @PUT(AppConstants.RetrofitApis.UpdateAlertDetails)
    fun UpdateAlertDetails(
        @Header("Authorization") authorizationValue: String,
        @Body alertRequest: AlertRequest
    ): Call<AlertDeleteResponse>
}