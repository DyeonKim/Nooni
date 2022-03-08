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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.nooni.adapter.ContactsRVAdapter
import com.ssafy.nooni.databinding.FragmentAllergyBinding
import com.ssafy.nooni.databinding.FragmentContactBinding
import com.ssafy.nooni.db.ContactDatabase
import com.ssafy.nooni.db.ContactViewModel
import com.ssafy.nooni.entity.Contact
import com.ssafy.nooni.ui.SelectDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ContactFragment"
class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private lateinit var contentResolver: ContentResolver
    private lateinit var contactsRVAdapter: ContactsRVAdapter
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private val model: ContactViewModel by activityViewModels()

    private var contactsList = mutableListOf<Contact>()
    var contact: Contact = Contact("", "", 0, 0)

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
        setRecyclerView()
    }

    private fun init(){
        contactsRVAdapter = ContactsRVAdapter(requireContext())

         model.getAll().observe(requireActivity(), Observer {
             contactsRVAdapter.setData(it)
             contactsRVAdapter.notifyDataSetChanged()
         })

        addContact()
        setRecyclerView()
    }

    private fun setRecyclerView() {

        binding.rvContactFContact.apply {
            adapter = contactsRVAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        }

        if(contactsList.isNotEmpty()) {
            contactsRVAdapter.setData(contactsList)
        }

        contactsRVAdapter.itemClickListener = object: ContactsRVAdapter.ItemClickListener {
            override fun onClick(contact: Contact) {
                TODO("Not yet implemented")
            }
        }

        contactsRVAdapter.itemLongClickListener = object: ContactsRVAdapter.ItemLongClickListener {
                override fun onClick(contact: Contact) {
                    showSelectDialog(contact)
                }
            }

    }

    private fun addContact(){
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
                lifecycleScope.launch(Dispatchers.IO){
                    model.insert(contact)
                }
            }
        }
        
        binding.imageButtonContactFAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            getResult.launch(intent)
        }
    }

    private fun showSelectDialog(contact: Contact){
        SelectDialog(requireContext())
            .setContent("해당 연락처를\n삭제하시겠습니까?")
            .setPositiveButtonText("삭제")
            .setOnPositiveClickListener{
                lifecycleScope.launch(Dispatchers.IO) {
                    model.delete(contact)
                }
            }.build().show()
    }
}