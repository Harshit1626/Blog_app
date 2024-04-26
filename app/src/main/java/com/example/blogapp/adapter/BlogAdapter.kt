package com.example.blogapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.example.blogapp.ReadMoreActivity
import com.example.blogapp.databinding.BlogItemBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlogAdapter(private val items: List<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-53b3f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from((parent.context))
        val binding = BlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)

    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = items[position]
        holder.bind(blogItem)

    }

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {
            val postId = blogItemModel.postId
            val context = binding.root.context
            binding.heading.text = blogItemModel.heading
            Glide.with(binding.profile.context).load(blogItemModel.profileImage)
                .into(binding.profile)
            binding.userName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likeCount.text = blogItemModel.likeCount.toString()
            // set on  click listner


            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }

            // check if user liked a post and update the like button image

            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")
            val currentUserLiked = currentUser?.uid?.let { uid ->
                postLikeReference.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.likeButton.setImageResource((R.drawable.heart_red))
                            } else {
                                binding.likeButton.setImageResource(R.drawable.heart_black)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

            }
            //handel like button click
            binding.likeButton.setOnClickListener {
                if (currentUser != null) {
                    handelLikeBUtoonClicked(postId, blogItemModel, binding)
                } else {
                    Toast.makeText(context, "You have to login First", Toast.LENGTH_SHORT).show()
                }
            }

        }

        private fun handelLikeBUtoonClicked(
            postId: String,
            blogItemModel: BlogItemModel,
            binding: BlogItemBinding
        ) {
            val userReference = databaseReference.child(
                "users" +
                        ""
            ).child(currentUser!!.uid)
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")

            postLikeReference.child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            userReference.child("likes").child(postId).removeValue()
                                .addOnSuccessListener {
                                    postLikeReference.child(currentUser.uid).removeValue()
                                    blogItemModel.likedBy?.remove(currentUser.uid)
                                    updateLikeButtonImage(binding, false)

                                    //decrement the like in the database
                                    val newLikeCount = blogItemModel.likeCount - 1
                                    blogItemModel.likeCount = newLikeCount
                                    databaseReference.child("blogs").child(postId)
                                        .child("likeCount").setValue(newLikeCount)
                                    notifyDataSetChanged()

                                }.addOnSuccessListener { e ->
                                    Log.e(
                                        "LikedClicked",
                                        "onDataChange: Failed to unlike the blog $e",
                                    )
                                }


                        } else {
                            //user not iked the post
                            userReference.child("likes").child(postId).setValue(true)
                                .addOnSuccessListener {
                                    postLikeReference.child(currentUser.uid).setValue(true)
                                    blogItemModel.likedBy?.add(currentUser.uid)
                                    updateLikeButtonImage(binding, true)

                                    val newLikeCount = blogItemModel.likeCount + 1
                                    blogItemModel.likeCount = newLikeCount
                                    databaseReference.child("blogs").child(postId)
                                        .child("likeCount").setValue(newLikeCount)
                                    notifyDataSetChanged()
                                }.addOnFailureListener { e ->
                                    Log.e(
                                        "LikedClicked",
                                        "onDataChange: Failed to unlike the blog $e",
                                    )

                                }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }


                })
        }

    }

    private fun updateLikeButtonImage(binding: BlogItemBinding, liked: Boolean) {
        if (liked) {
            binding.likeButton.setImageResource(R.drawable.heart_black)
        } else {
            binding.likeButton.setImageResource(R.drawable.heart_red)
        }


    }

}