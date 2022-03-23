package com.ssafy.nooni

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.nooni.adapter.ContactsRVAdapter
import com.ssafy.nooni.databinding.FragmentContactBinding
import com.ssafy.nooni.db.ContactViewModel
import com.ssafy.nooni.entity.Contact
import com.ssafy.nooni.ui.SelectDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "ContactFragment"
class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private lateinit var contentResolver: ContentResolver
    private lateinit var contactsRVAdapter: ContactsRVAdapter
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private val model: ContactViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    private var contactsList = mutableListOf<Contact>()
    var contact: Contact = Contact("", "", 0, 0)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

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
        contactsRVAdapter = ContactsRVAdapter(requireContext())

         model.getAll().observe(requireActivity(), Observer {
             contactsRVAdapter.setData(it)
             contactsRVAdapter.notifyDataSetChanged()
         })

        addContact()
        setRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.findViewById<TextView>(R.id.tv_title).text = "연락처"
        mainActivity.ttsSpeak(resources.getString(R.string.ContactFrag))
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
                mainActivity.ttsSpeak(contact.name + resources.getString(R.string.ContactCall))
                showSelectDialog(contact)
            }
        }

        contactsRVAdapter.itemLongClickListener = object: ContactsRVAdapter.ItemLongClickListener {
            override fun onClick(contact: Contact) {
                mainActivity.ttsSpeak(contact.name + resources.getString(R.string.ContactRemoveAsk))
                showDeleteDialog(contact)
            }
        }
    }

    private fun moveDial(contact: Contact) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${contact.phone}")
//        intent.putExtra("videocall", true) // 인텐트를 Intent.ACTION_CALL로 주고 요 엑스트라를 넣으면 영상통화가 걸려야 되는데 일반 전화로 걸림..
        startActivity(intent)
    }

    private fun addContact(){
        val handler = Handler(Looper.getMainLooper())
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
                    try {
                        model.insert(contact)
                        mainActivity.ttsSpeak(resources.getString(R.string.ContactAddFinish))
                        handler.postDelayed(Runnable{
                            Toast.makeText(mainActivity, resources.getString(R.string.ContactAddFinish), Toast.LENGTH_SHORT).show()
                        }, 0)
                    }catch (e: SQLiteConstraintException){
                        mainActivity.ttsSpeak(resources.getString(R.string.ContactAlreadyAdd))
                        handler.postDelayed(Runnable{
                            Toast.makeText(mainActivity, resources.getString(R.string.ContactAlreadyAdd), Toast.LENGTH_SHORT).show()
                        }, 0)
                    }
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
            .setContent("다이얼 화면으로\n이동하시겠습니까?")
            .setOnNegativeClickListener{
                mainActivity.tts.stop()
            }
            .setOnPositiveClickListener{
                moveDial(contact)
            }.build().show()
    }

    private fun showDeleteDialog(contact: Contact){
        val handler = Handler(Looper.getMainLooper())
        SelectDialog(requireContext())
            .setContent("해당 연락처를\n삭제하시겠습니까?")
            .setOnNegativeClickListener{
                mainActivity.tts.stop()
            }
            .setPositiveButtonText("삭제")
            .setOnPositiveClickListener{
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        model.delete(contact)
                        mainActivity.ttsSpeak(resources.getString(R.string.ContactRemove))
                        handler.postDelayed(Runnable{
                            Toast.makeText(mainActivity, resources.getString(R.string.ContactRemove), Toast.LENGTH_SHORT).show()
                        }, 0)
                    } catch (e: Exception) {
                        Log.e(TAG, "showDeleteDialog: ERROR = ${e.message}")
                    }
                }
            }.build().show()
    }
}