package com.example.contacts


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(var context: Context, var contactsList: MutableList<TableFields>) : RecyclerView.Adapter<ContactViewHolder>() {

    var databaseObject: Database = Database(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val contactInflater = LayoutInflater.from(context)
        val contactView = contactInflater.inflate(R.layout.contact_card, parent, false)
        return ContactViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactsList[position]
        holder.ContactName.text = contact.contact_name
        holder.ContactNumber.text = contact.contact_number

        holder.itemView.setOnClickListener {
            selectOperations(it, contact, position)
        }
    }

    private fun selectOperations(opView: View?, contact: TableFields, position: Int) {
        val popupInflater = LayoutInflater.from(context)
        val popupView = popupInflater.inflate(R.layout.option_cardview, null)

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val width = opView?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT

        val popupWindow = PopupWindow(popupView, width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val call = popupView.findViewById<ImageView>(R.id.call_contact)
        val msg = popupView.findViewById<ImageView>(R.id.msg_contact)
        val edit = popupView.findViewById<ImageView>(R.id.edit_contact)
        val delete = popupView.findViewById<ImageView>(R.id.delete_contact)

        call.setOnClickListener {
            DialCall(contact.contact_number)
            popupWindow.dismiss()
        }

        msg.setOnClickListener {
            SendSMS(contact.contact_number)
            popupWindow.dismiss()
        }

        edit.setOnClickListener {
            EditNumber(contact)
            popupWindow.dismiss()
        }

        delete.setOnClickListener {
            databaseObject.DeleteDatabse(contact.contact_id)
            contactsList.remove(contact)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, contactsList.size)
            val intent = Intent(context, ViewContacts::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(opView, Gravity.CENTER, 0, 0)
    }

    private fun DialCall(phoneNumber: String) {
        if (phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun SendSMS(phoneNumber: String) {
        if (phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("sms:$phoneNumber")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun EditNumber(contact: TableFields) {
        val intent = Intent(context, UpdateContact::class.java)
        intent.putExtra("contact_id", contact.contact_id)
        intent.putExtra("contact_name", contact.contact_name)
        intent.putExtra("contact_number", contact.contact_number)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

class ContactViewHolder (contacts : View) : RecyclerView.ViewHolder(contacts)
{
    lateinit var ContactName : TextView
    lateinit var ContactNumber : TextView

    init {
        ContactName = contacts.findViewById(R.id.contact_name)
        ContactNumber = contacts.findViewById(R.id.contact_number)
    }
}