package com.example.roomdatabase

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabase.databinding.ActivityMainBinding
import com.example.roomdatabase.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val employeeDao=(application as EmployeeApp).db.employeeDao()
         binding.btnAddRecord.setOnClickListener {
            addRecord(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect {
                val list=ArrayList(it)
                setUpListOfDataIntoRecyclerView(list,employeeDao)
            }
        }

    }

    fun addRecord(employeeDao: EmployeeDao){
        val name=binding.etName.text.toString()
        val email=binding.etEmail.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty())
        {
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext,"Record Saved",Toast.LENGTH_SHORT).show()
                binding.etEmail.text.clear()
                binding.etName.text.clear()
            }
        }else{
            Toast.makeText(applicationContext,"Name & Email is Empty",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpListOfDataIntoRecyclerView(
        employeeList:ArrayList<EmployeeEntity>,
        employeeDao: EmployeeDao)
    {
        if (employeeList.isNotEmpty()){
            val itemAdapter = ItemAdapter(employeeList,
                {
                 upDateId->
                updateRecordDialog(upDateId,employeeDao)
            },
                {
                    deleteId->
                    lifecycleScope.launch {
                        employeeDao.fetchEmployeeById(deleteId).collect {
                            if (it != null){
                                deleteRecordAlertDialog(deleteId,employeeDao,it)
                            }
                        }
                    }
                })
            binding.rvRecordList.layoutManager = LinearLayoutManager(this)
            binding.rvRecordList.adapter=itemAdapter
            binding.rvRecordList.visibility= View.VISIBLE
            binding.tvNoRecord.visibility=View.GONE
        }else{
            binding.rvRecordList.visibility=View.GONE
            binding.tvNoRecord.visibility=View.VISIBLE
        }

    }


    private fun updateRecordDialog(id:Int,employeeDao: EmployeeDao){
        val updateDialog= Dialog(this,R.style.Theme_dialog)
        updateDialog.setCancelable(false)

        val binding=DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if(it != null){
                    binding.etUpdateName.setText(it.name)
                    binding.etUpdateEmail.setText(it.email)
                }

            }
        }

        binding.tvUpdate.setOnClickListener {
            val name=binding.etUpdateName.text.toString()
            val email=binding.etUpdateEmail.text.toString()


            if (name.isNotEmpty() && email.isNotEmpty()){

                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id,name,email))
                    Toast.makeText(applicationContext,"Record Update",Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss()
                }
            }else{
                Toast.makeText(applicationContext,"Name & Email is Empty",Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }
    private fun deleteRecordAlertDialog(id:Int,employeeDao: EmployeeDao,employee: EmployeeEntity){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure you wants to delete ${employee.name}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("yes"){d,_ ->
                lifecycleScope.launch {
                    employeeDao.delete(EmployeeEntity(id))
                    Toast.makeText(this@MainActivity, "Record Deleted", Toast.LENGTH_SHORT).show()
                    d.dismiss()
                }
            }
            .setNegativeButton("No"){d,_ ->
                d.dismiss()
            }
        val alertDialog: AlertDialog= builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}