package com.example.university

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.university.databinding.ItemDepartmentBinding
import com.example.university.model.MedicalDepartmentItem

// 진료과목을 선택하는 RecyclerView
class MedicalDepartmentAdapter(
    private val context: Context,
    private val items: List<MedicalDepartmentItem>,
    private val vm: MapsViewModel
) :
    RecyclerView.Adapter<MedicalDepartmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalDepartmentViewHolder {
        // ViewBinding이 활성화되어 있길래 사용하였습니다. 혹시 문제가 있으시다면 말씀해주세요
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDepartmentBinding.inflate(inflater, parent, false)

        return MedicalDepartmentViewHolder(binding, vm)
    }

    override fun onBindViewHolder(holder: MedicalDepartmentViewHolder, position: Int) {
        holder.bind(context, items[position])
    }

    override fun getItemCount() = items.size
}

class MedicalDepartmentViewHolder(
    private val binding: ItemDepartmentBinding,
    private val vm: MapsViewModel
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(context: Context, item: MedicalDepartmentItem) {
        binding.apply {
            departmentName.text = item.name
            root.setOnClickListener {
                vm.selectedDepartment.value = if (vm.selectedDepartment.value != item) item else null
            }

            vm.selectedDepartment.observeForever {
                if (item == it) {
                    root.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
                } else {
                    root.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey))
                }
            }
        }
    }
}
