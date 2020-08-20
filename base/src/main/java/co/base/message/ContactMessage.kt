package co.base.message

import com.google.gson.annotations.SerializedName

data class ContactMessage(

	@field:SerializedName("view")
	val view: String? = null,

	@field:SerializedName("other")
	val other: Any? = null,

	@field:SerializedName("contact_message_id")
	val contactMessageId: Int? = null,

	@field:SerializedName("subject")
	val subject: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("reply")
	val reply: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
