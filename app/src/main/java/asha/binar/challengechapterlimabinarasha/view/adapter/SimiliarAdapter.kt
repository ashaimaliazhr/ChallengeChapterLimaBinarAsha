package asha.binar.challengechapterlimabinarasha.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import asha.binar.challengechapterlimabinarasha.databinding.ItemSimiliarBinding
import asha.binar.challengechapterlimabinarasha.model.popularmovie.ResultMovie
import com.bumptech.glide.Glide

class SimiliarAdapter(val onItemClick: OnClickListener) : RecyclerView.Adapter<SimiliarAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: ItemSimiliarBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResultMovie){
            binding.apply {
                val baseUrlImg = "https://image.tmdb.org/t/p/w500/"
                val urlImage = baseUrlImg + data.backdropPath

                tvMovie.text = data.title
                Glide.with(itemView).load(urlImage).into(imgMovie)

                root.setOnClickListener {
                    onItemClick.onClickItem(data)
                }
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<ResultMovie>(){
        override fun areItemsTheSame(oldItem: ResultMovie, newItem: ResultMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResultMovie, newItem: ResultMovie): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(value: List<ResultMovie>?) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSimiliarBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = differ.currentList[position]
        data.let {
            holder.bind(data)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnClickListener{
        fun onClickItem(data: ResultMovie)
    }
}