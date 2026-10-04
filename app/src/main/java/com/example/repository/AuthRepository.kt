package com.example.repository

import com.example.data.MockData
import com.example.firebase.FirebaseConfig
import com.example.model.ShippingAddress
import com.example.model.UserProfile
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: UserProfile) : AuthState()
    object Guest : AuthState()
    data class Error(val message: String) : AuthState()
}

interface AuthRepository {
    val authState: StateFlow<AuthState>
    val currentUser: UserProfile?
    val isGuest: Boolean
    suspend fun signInWithEmail(email: String, pass: String): Result<UserProfile>
    suspend fun signUpWithEmail(name: String, email: String, pass: String): Result<UserProfile>
    suspend fun continueAsGuest()
    suspend fun signOut()
    suspend fun updateUserProfile(profile: UserProfile): Result<Unit>
    fun clearError()
}

class FirebaseAuthRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var _cachedUser: UserProfile? = null
    override val currentUser: UserProfile?
        get() = _cachedUser

    private var _isGuestMode: Boolean = false
    override val isGuest: Boolean
        get() = _isGuestMode

    private val auth: FirebaseAuth?
        get() = FirebaseConfig.getAuth()

    private val firestore: FirebaseFirestore?
        get() = FirebaseConfig.getFirestore()

    init {
        // Listen to Firebase Auth state if available
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            val user = firebaseAuth.currentUser
            if (user != null) {
                scope.launch {
                    loadUserProfileFromFirestore(user)
                }
            } else {
                _authState.value = AuthState.Idle
            }
        } else {
            // Default to mock authenticated for demo or idle
            _cachedUser = MockData.sampleUser
            _authState.value = AuthState.Authenticated(MockData.sampleUser)
        }
    }

    override suspend fun signInWithEmail(email: String, pass: String): Result<UserProfile> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val errorMsg = "Please enter a valid email address."
            _authState.value = AuthState.Error(errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }
        if (pass.isBlank()) {
            val errorMsg = "Please enter your password."
            _authState.value = AuthState.Error(errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }

        _authState.value = AuthState.Loading

        val firebaseAuth = auth
        if (firebaseAuth == null) {
            // Firebase not configured yet: simulate successful local sign-in with mock user
            _isGuestMode = false
            val mockUser = MockData.sampleUser.copy(email = trimmedEmail)
            _cachedUser = mockUser
            _authState.value = AuthState.Authenticated(mockUser)
            return Result.success(mockUser)
        }

        return withContext(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(trimmedEmail, pass).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val profile = loadUserProfileFromFirestore(firebaseUser)
                    _isGuestMode = false
                    _cachedUser = profile
                    _authState.value = AuthState.Authenticated(profile)
                    Result.success(profile)
                } else {
                    val err = "Authentication failed. Please verify credentials."
                    _authState.value = AuthState.Error(err)
                    Result.failure(Exception(err))
                }
            } catch (e: Throwable) {
                val friendlyMessage = mapAuthExceptionToMessage(e)
                _authState.value = AuthState.Error(friendlyMessage)
                Result.failure(Exception(friendlyMessage, e))
            }
        }
    }

    override suspend fun signUpWithEmail(name: String, email: String, pass: String): Result<UserProfile> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            val errorMsg = "Please enter your full name."
            _authState.value = AuthState.Error(errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val errorMsg = "Please enter a valid email address."
            _authState.value = AuthState.Error(errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }
        if (pass.length < 6) {
            val errorMsg = "Password must be at least 6 characters long."
            _authState.value = AuthState.Error(errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }

        _authState.value = AuthState.Loading

        val firebaseAuth = auth
        if (firebaseAuth == null) {
            // Firebase not configured yet: simulate sign-up
            _isGuestMode = false
            val newUser = UserProfile(
                id = "usr_" + System.currentTimeMillis(),
                name = trimmedName,
                email = trimmedEmail,
                phone = "+1 555-0100",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&q=80",
                membershipTier = "Member",
                defaultAddress = ShippingAddress(
                    fullName = trimmedName,
                    street = "742 Evergreen Terrace",
                    city = "Springfield",
                    state = "OR",
                    zipCode = "97477",
                    phone = "+1 555-0100"
                )
            )
            _cachedUser = newUser
            _authState.value = AuthState.Authenticated(newUser)
            return Result.success(newUser)
        }

        return withContext(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, pass).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val newProfile = UserProfile(
                        id = firebaseUser.uid,
                        name = trimmedName,
                        email = trimmedEmail,
                        phone = firebaseUser.phoneNumber ?: "",
                        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&q=80",
                        membershipTier = "Member",
                        defaultAddress = ShippingAddress(
                            fullName = trimmedName,
                            street = "123 Fashion Blvd",
                            city = "New York",
                            state = "NY",
                            zipCode = "10001",
                            phone = "+1 555-0199"
                        ),
                        createdAt = System.currentTimeMillis()
                    )
                    saveUserProfileToFirestore(newProfile)
                    _isGuestMode = false
                    _cachedUser = newProfile
                    _authState.value = AuthState.Authenticated(newProfile)
                    Result.success(newProfile)
                } else {
                    val err = "Failed to create account. Please try again."
                    _authState.value = AuthState.Error(err)
                    Result.failure(Exception(err))
                }
            } catch (e: Throwable) {
                val friendlyMessage = mapAuthExceptionToMessage(e)
                _authState.value = AuthState.Error(friendlyMessage)
                Result.failure(Exception(friendlyMessage, e))
            }
        }
    }

    override suspend fun continueAsGuest() {
        _isGuestMode = true
        val guestUser = UserProfile(
            id = "guest_user",
            name = "Guest Client",
            email = "guest@aurafashion.com",
            phone = "",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&q=80",
            membershipTier = "Guest Client",
            defaultAddress = ShippingAddress(
                fullName = "Guest Client",
                street = "123 Fashion Blvd",
                city = "New York",
                state = "NY",
                zipCode = "10001",
                phone = "+1 555-0100"
            ),
            isGuest = true
        )
        _cachedUser = guestUser
        _authState.value = AuthState.Guest
    }

    override suspend fun signOut() {
        try {
            auth?.signOut()
        } catch (ignored: Exception) {}
        _cachedUser = null
        _isGuestMode = false
        _authState.value = AuthState.Idle
    }

    override suspend fun updateUserProfile(profile: UserProfile): Result<Unit> {
        _cachedUser = profile
        if (_authState.value is AuthState.Authenticated) {
            _authState.value = AuthState.Authenticated(profile)
        }
        val db = firestore
        if (db != null && !_isGuestMode && profile.id != "guest_user") {
            return withContext(Dispatchers.IO) {
                try {
                    saveUserProfileToFirestore(profile)
                    Result.success(Unit)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
        return Result.success(Unit)
    }

    override fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = if (_cachedUser != null) {
                if (_isGuestMode) AuthState.Guest else AuthState.Authenticated(_cachedUser!!)
            } else {
                AuthState.Idle
            }
        }
    }

    private suspend fun loadUserProfileFromFirestore(firebaseUser: FirebaseUser): UserProfile {
        val db = firestore
        if (db != null) {
            try {
                val doc = db.collection("users").document(firebaseUser.uid).get().await()
                if (doc.exists()) {
                    val addrMap = doc.get("defaultAddress") as? Map<*, *>
                    val addr = if (addrMap != null) {
                        ShippingAddress(
                            fullName = addrMap["fullName"] as? String ?: firebaseUser.displayName ?: "Client",
                            street = addrMap["street"] as? String ?: "",
                            city = addrMap["city"] as? String ?: "",
                            state = addrMap["state"] as? String ?: "",
                            zipCode = addrMap["zipCode"] as? String ?: "",
                            phone = addrMap["phone"] as? String ?: ""
                        )
                    } else {
                        MockData.sampleUser.defaultAddress
                    }
                    val profile = UserProfile(
                        id = firebaseUser.uid,
                        name = doc.getString("name") ?: firebaseUser.displayName ?: "Client",
                        email = doc.getString("email") ?: firebaseUser.email ?: "",
                        phone = doc.getString("phone") ?: firebaseUser.phoneNumber ?: "",
                        avatarUrl = doc.getString("avatarUrl") ?: MockData.sampleUser.avatarUrl,
                        membershipTier = doc.getString("membershipTier") ?: "Member",
                        defaultAddress = addr,
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        isGuest = false
                    )
                    _cachedUser = profile
                    _authState.value = AuthState.Authenticated(profile)
                    return profile
                }
            } catch (t: Throwable) {
                // Ignore and fall back to local profile
            }
        }

        val fallback = UserProfile(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "Client",
            email = firebaseUser.email ?: "",
            phone = firebaseUser.phoneNumber ?: "",
            avatarUrl = MockData.sampleUser.avatarUrl,
            membershipTier = "Member",
            defaultAddress = MockData.sampleUser.defaultAddress,
            isGuest = false
        )
        _cachedUser = fallback
        _authState.value = AuthState.Authenticated(fallback)
        return fallback
    }

    private suspend fun saveUserProfileToFirestore(profile: UserProfile) {
        val db = firestore ?: return
        val data = hashMapOf(
            "id" to profile.id,
            "name" to profile.name,
            "email" to profile.email,
            "phone" to profile.phone,
            "avatarUrl" to profile.avatarUrl,
            "membershipTier" to profile.membershipTier,
            "defaultAddress" to hashMapOf(
                "fullName" to profile.defaultAddress.fullName,
                "street" to profile.defaultAddress.street,
                "city" to profile.defaultAddress.city,
                "state" to profile.defaultAddress.state,
                "zipCode" to profile.defaultAddress.zipCode,
                "phone" to profile.defaultAddress.phone
            ),
            "createdAt" to profile.createdAt
        )
        db.collection("users").document(profile.id).set(data).await()
    }

    private fun mapAuthExceptionToMessage(e: Throwable): String {
        return when (e) {
            is FirebaseAuthInvalidUserException -> "No account found with this email. Please check your email or sign up."
            is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password. Please verify your credentials."
            is FirebaseAuthUserCollisionException -> "An account with this email already exists. Please sign in."
            is FirebaseAuthWeakPasswordException -> "Password is too weak. Please use at least 6 characters."
            is FirebaseNetworkException -> "Network connection issue. Please check your internet connection and try again."
            is FirebaseTooManyRequestsException -> "Too many unsuccessful attempts. Please try again in a few moments."
            else -> e.localizedMessage ?: "Authentication failed. Please check your network and credentials."
        }
    }
}

class InMemoryAuthRepository(
    initialUser: UserProfile = MockData.sampleUser
) : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated(initialUser))
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var _cachedUser: UserProfile? = initialUser
    override val currentUser: UserProfile?
        get() = _cachedUser

    private var _isGuestMode: Boolean = false
    override val isGuest: Boolean
        get() = _isGuestMode

    override suspend fun signInWithEmail(email: String, pass: String): Result<UserProfile> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val errorMsg = "Please enter a valid email address."
            _authState.value = AuthState.Error(errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }
        if (pass.isBlank()) {
            val errorMsg = "Please enter your password."
            _authState.value = AuthState.Error(errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }
        _isGuestMode = false
        val user = MockData.sampleUser.copy(email = trimmedEmail)
        _cachedUser = user
        _authState.value = AuthState.Authenticated(user)
        return Result.success(user)
    }

    override suspend fun signUpWithEmail(name: String, email: String, pass: String): Result<UserProfile> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        if (trimmedName.isBlank()) {
            val err = "Name cannot be empty."
            _authState.value = AuthState.Error(err)
            return Result.failure(IllegalArgumentException(err))
        }
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val err = "Please enter a valid email address."
            _authState.value = AuthState.Error(err)
            return Result.failure(IllegalArgumentException(err))
        }
        if (pass.length < 6) {
            val err = "Password must be at least 6 characters."
            _authState.value = AuthState.Error(err)
            return Result.failure(IllegalArgumentException(err))
        }
        _isGuestMode = false
        val newUser = MockData.sampleUser.copy(name = trimmedName, email = trimmedEmail)
        _cachedUser = newUser
        _authState.value = AuthState.Authenticated(newUser)
        return Result.success(newUser)
    }

    override suspend fun continueAsGuest() {
        _isGuestMode = true
        val guest = UserProfile(
            id = "user_guest",
            name = "Guest Client",
            email = "guest@aurafashion.com",
            phone = "",
            avatarUrl = "",
            membershipTier = "Guest Client",
            defaultAddress = MockData.sampleAddress,
            isGuest = true
        )
        _cachedUser = guest
        _authState.value = AuthState.Guest
    }

    override suspend fun signOut() {
        _cachedUser = null
        _isGuestMode = false
        _authState.value = AuthState.Idle
    }

    override suspend fun updateUserProfile(profile: UserProfile): Result<Unit> {
        _cachedUser = profile
        _authState.value = AuthState.Authenticated(profile)
        return Result.success(Unit)
    }

    override fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = if (_cachedUser != null) {
                if (_isGuestMode) AuthState.Guest else AuthState.Authenticated(_cachedUser!!)
            } else {
                AuthState.Idle
            }
        }
    }
}

