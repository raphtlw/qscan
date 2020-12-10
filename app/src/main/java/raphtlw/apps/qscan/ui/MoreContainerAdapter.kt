package raphtlw.apps.qscan.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import raphtlw.apps.qscan.data.MoreItem
import raphtlw.apps.qscan.databinding.MoreItemBinding

class MoreContainerAdapter(private val dataset: Array<MoreItem>) : RecyclerView.Adapter<MoreContainerAdapter.ViewHolder>() {

    private lateinit var binding: MoreItemBinding

    inner class ViewHolder(binding: MoreItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding.itemName.text = dataset[position].name
        binding.root.setOnClickListener(dataset[position].onClick)
    }

    override fun getItemCount() = dataset.size
}