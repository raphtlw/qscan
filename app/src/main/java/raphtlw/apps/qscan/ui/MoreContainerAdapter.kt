package raphtlw.apps.qscan.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.more_item.view.*
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.general.MoreItem

class MoreContainerAdapter(private val dataset: Array<MoreItem>) : RecyclerView.Adapter<MoreContainerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.more_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.item_name.text = dataset[position].name
        holder.itemView.setOnClickListener(dataset[position].onClick)
    }

    override fun getItemCount() = dataset.size
}