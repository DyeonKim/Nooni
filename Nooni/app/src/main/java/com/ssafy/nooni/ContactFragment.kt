package com.ssafy.nooni

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ssafy.nooni.databinding.FragmentAllergyBinding
import com.ssafy.nooni.databinding.FragmentContactBinding
import com.ssafy.nooni.entity.Contact

private const val TAG = "ContactFragment"
class ContactFragment : Fragment() {
    lateinit var binding: FragmentContactBinding
    private lateinit var contentResolver: ContentResolver
    var contact: Contact = Contact("", "", 0, 0)
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK){
                contentResolver = requireActivity().contentResolver
                val cursor: Cursor? = contentResolver.query(
                    it.data!!.data!!, arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.Contacts.PHOTO_ID,
                        ContactsContract.Contacts._ID
                    ), null, null, null
                )
                cursor!!.moveToFirst()
                contact.name = cursor!!.getString(0)
                contact.phone = cursor!!.getString(1)
                contact.photo_id = cursor!!.getLong(2)
                contact.person_id = cursor!!.getLong(3)
                cursor.close()

                Log.d(TAG, "init: phone = ${contact.name}")
            }
        }
        
        binding.imageButtonContactFAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            getResult.launch(intent)
        }

    }



}