// Aura Fashion - Interactive Jetpack Compose App Simulator
const state = {
  theme: 'light',
  currentScreen: 'home',
  screenHistory: ['home'],
  selectedCategory: 'All',
  searchQuery: '',
  activeArg: null,
  wishlist: new Set(['prod_1', 'prod_5']),
  cart: [
    { id: 'cart_1', productId: 'prod_1', size: 'M', color: 'Warm Camel', qty: 1 },
    { id: 'cart_2', productId: 'prod_5', size: 'Standard', color: 'Onyx Black', qty: 1 }
  ],
  orders: [
    { id: 'ord_1', num: 'AUR-92841', date: 'Oct 2, 2026', total: 555.0, status: 'Processing', items: ['prod_2', 'prod_3'] },
    { id: 'ord_2', num: 'AUR-87123', date: 'Sep 18, 2026', total: 135.0, status: 'Delivered', items: ['prod_7'] }
  ]
};

const categories = [
  { id: 'all', name: 'All', img: 'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=600&q=80', count: 8 },
  { id: 'women', name: 'Women', img: 'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=600&q=80', count: 3 },
  { id: 'men', name: 'Men', img: 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=600&q=80', count: 2 },
  { id: 'outerwear', name: 'Outerwear', img: 'https://images.unsplash.com/photo-1544441893-675973e31985?w=600&q=80', count: 2 },
  { id: 'footwear', name: 'Footwear', img: 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=600&q=80', count: 1 },
  { id: 'accessories', name: 'Accessories', img: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&q=80', count: 2 }
];

const products = [
  {
    id: 'prod_1',
    name: 'Cashmere Blend Oversized Trench',
    category: 'Outerwear',
    price: 389.0,
    originalPrice: 499.0,
    discount: 22,
    rating: 4.9,
    reviews: 128,
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    colors: ['Warm Camel', 'Onyx Black', 'Muted Sage'],
    img: 'https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=800&q=80',
    featured: true,
    desc: 'A quintessential timeless statement piece crafted from double-faced Italian wool-cashmere blend. Features horn buttons and storm flap.'
  },
  {
    id: 'prod_2',
    name: 'Structured Silk Crepe Blazer',
    category: 'Women',
    price: 265.0,
    originalPrice: 320.0,
    discount: 17,
    rating: 4.8,
    reviews: 94,
    sizes: ['S', 'M', 'L'],
    colors: ['Onyx Black', 'Alabaster Cream'],
    img: 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=800&q=80',
    featured: true,
    desc: 'Sharp peak lapels with subtle shoulder pads create an empowering structured drape from boardroom to gallery opening.'
  },
  {
    id: 'prod_3',
    name: 'Merino Wool Ribbed Turtleneck',
    category: 'Women',
    price: 145.0,
    originalPrice: 180.0,
    discount: 19,
    rating: 4.7,
    reviews: 210,
    sizes: ['XS', 'S', 'M', 'L'],
    colors: ['Alabaster Cream', 'Muted Sage'],
    img: 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=800&q=80',
    featured: false,
    desc: 'Ultra-fine Australian merino wool knitted in a delicate tactile rib. Breathable, thermal-regulating, and exceptionally soft.'
  },
  {
    id: 'prod_4',
    name: 'Tailored Wool Flannel Trousers',
    category: 'Men',
    price: 195.0,
    originalPrice: 240.0,
    discount: 18,
    rating: 4.8,
    reviews: 82,
    sizes: ['30', '32', '34', '36'],
    colors: ['Charcoal', 'Midnight Navy'],
    img: 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=800&q=80',
    featured: false,
    desc: 'Modern straight-leg trousers with crisp front pleats and adjustable side waist tabs. Brushed wool flannel.'
  },
  {
    id: 'prod_5',
    name: 'Monochrome Minimalist Leather Tote',
    category: 'Accessories',
    price: 310.0,
    originalPrice: 390.0,
    discount: 20,
    rating: 4.9,
    reviews: 315,
    sizes: ['Standard'],
    colors: ['Onyx Black', 'Warm Camel'],
    img: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=800&q=80',
    featured: true,
    desc: 'Sculpted from full-grain calfskin with hand-painted raw edges. Spacious suede-lined interior with padded sleeve.'
  },
  {
    id: 'prod_6',
    name: 'Chunky Leather Chelsea Boots',
    category: 'Footwear',
    price: 280.0,
    originalPrice: 350.0,
    discount: 20,
    rating: 4.6,
    reviews: 76,
    sizes: ['39', '40', '41', '42'],
    colors: ['Onyx Black'],
    img: 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=800&q=80',
    featured: false,
    desc: 'Bold contemporary silhouette set on a lightweight lugged Vibram sole. Elasticated side gussets and pull tabs.'
  },
  {
    id: 'prod_7',
    name: 'Heavyweight Organic Cotton Hoodie',
    category: 'Men',
    price: 120.0,
    originalPrice: 150.0,
    discount: 20,
    rating: 4.9,
    reviews: 430,
    sizes: ['S', 'M', 'L', 'XL'],
    colors: ['Muted Sage', 'Charcoal'],
    img: 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800&q=80',
    featured: true,
    desc: '450 GSM organic French terry cotton with brushed interior. Relaxed drop-shoulder silhouette with kangaroo pocket.'
  },
  {
    id: 'prod_8',
    name: 'Asymmetric Pleated Midi Skirt',
    category: 'Women',
    price: 175.0,
    originalPrice: 210.0,
    discount: 16,
    rating: 4.8,
    reviews: 63,
    sizes: ['XS', 'S', 'M'],
    colors: ['Terracotta', 'Midnight Navy'],
    img: 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=800&q=80',
    featured: false,
    desc: 'Dramatic sunray pleating with an architectural handkerchief hem. Fluid recycled crepe.'
  }
];

function toggleTheme() {
  state.theme = state.theme === 'light' ? 'dark' : 'light';
  document.getElementById('deviceContainer').setAttribute('data-theme', state.theme);
}

function updateBadges() {
  const count = state.cart.reduce((s, i) => s + i.qty, 0);
  const badge = document.getElementById('cartBadge');
  if (badge) {
    badge.innerText = count;
    badge.style.display = count > 0 ? 'inline-block' : 'none';
  }
}

function toggleWishlist(prodId, ev) {
  if (ev) ev.stopPropagation();
  if (state.wishlist.has(prodId)) {
    state.wishlist.delete(prodId);
  } else {
    state.wishlist.add(prodId);
  }
  renderScreen();
}

function navigateTo(screen, arg = null) {
  state.currentScreen = screen;
  state.activeArg = arg;
  if (state.screenHistory[state.screenHistory.length - 1] !== screen) {
    state.screenHistory.push(screen);
  }
  renderScreen();
}

function handleBackNav() {
  if (state.screenHistory.length > 1) {
    state.screenHistory.pop();
    state.currentScreen = state.screenHistory[state.screenHistory.length - 1];
    renderScreen();
  }
}

function openSearch() {
  navigateTo('home');
  setTimeout(() => {
    const inp = document.getElementById('mainSearchInput');
    if (inp) inp.focus();
  }, 50);
}

function renderScreen() {
  updateBadges();
  const content = document.getElementById('screenContent');
  const title = document.getElementById('topbarTitle');
  const backBtn = document.getElementById('navBackBtn');
  const bottomNav = document.getElementById('bottomNav');
  if (!content) return;

  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  const activeIdx = ['home', 'categories', 'wishlist', 'cart', 'profile'].indexOf(state.currentScreen);
  if (activeIdx >= 0) {
    document.querySelectorAll('.nav-item')[activeIdx]?.classList.add('active');
    if (bottomNav) bottomNav.style.display = 'flex';
  } else {
    if (bottomNav) bottomNav.style.display = 'none';
  }

  if (backBtn) {
    backBtn.style.display = (state.currentScreen !== 'home') ? 'flex' : 'none';
  }

  switch (state.currentScreen) {
    case 'home':
      if (title) title.innerText = 'A U R A';
      renderHome(content);
      break;
    case 'categories':
      if (title) title.innerText = 'COLLECTIONS';
      renderCategories(content);
      break;
    case 'listing':
      if (title) title.innerText = (state.activeArg || 'All').toUpperCase();
      renderListing(content);
      break;
    case 'details':
      if (title) title.innerText = 'DETAILS';
      renderDetails(content);
      break;
    case 'wishlist':
      if (title) title.innerText = 'MY WISHLIST';
      renderWishlist(content);
      break;
    case 'cart':
      if (title) title.innerText = 'SHOPPING BAG';
      renderCart(content);
      break;
    case 'checkout':
      if (title) title.innerText = 'CHECKOUT';
      renderCheckout(content);
      break;
    case 'orders':
      if (title) title.innerText = 'MY ORDERS';
      renderOrders(content);
      break;
    case 'profile':
      if (title) title.innerText = 'PROFILE';
      renderProfile(content);
      break;
  }
}

function renderHome(container) {
  const filtered = products.filter(p => {
    const matchesCat = state.selectedCategory === 'All' || p.category === state.selectedCategory;
    const matchesQ = !state.searchQuery || p.name.toLowerCase().includes(state.searchQuery.toLowerCase());
    return matchesCat && matchesQ;
  });

  let topSectionHtml = '';
  if (!state.searchQuery) {
    topSectionHtml = `
      <div class="hero-banner" onclick="navigateTo('listing', 'Outerwear')">
        <img src="https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1000&q=80" alt="Autumn collection">
        <div class="banner-overlay">
          <span class="banner-tag">NEW SEASON</span>
          <div class="banner-title">AUTUMN / WINTER 2026</div>
          <div class="banner-desc">Double-faced cashmere & Italian wool tailoring</div>
          <button class="banner-btn">Explore Edit</button>
        </div>
      </div>

      <div class="chips-row">
        ${categories.map(c => `
          <div class="chip ${state.selectedCategory === c.name ? 'active' : ''}" onclick="state.selectedCategory='${c.name}'; renderScreen()">
            ${c.name}
          </div>
        `).join('')}
      </div>

      <div class="section-header">
        <div>
          <div class="section-title">Featured Pieces</div>
          <div class="section-sub">Autumn editorial highlights</div>
        </div>
        <button class="see-all-btn" onclick="navigateTo('listing', 'All')">See All</button>
      </div>
    `;
  }

  const clearBtn = state.searchQuery ? '<button onclick="state.searchQuery=\'\'; renderScreen()" style="background:none; border:none; cursor:pointer; color:var(--on-surface);">✕</button>' : '';

  container.innerHTML = `
    <div class="search-box">
      <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
      <input id="mainSearchInput" class="search-input" placeholder="Search coats, cashmere, silk..." value="${state.searchQuery}" oninput="state.searchQuery=this.value; renderHome(document.getElementById('screenContent'))">
      ${clearBtn}
    </div>

    ${topSectionHtml}

    <div class="products-grid">
      ${filtered.map(p => `
        <div class="product-card" onclick="navigateTo('details', '${p.id}')">
          <div class="product-img-wrap">
            <img src="${p.img}" alt="${p.name}">
            ${p.discount ? `<span class="discount-badge">-${p.discount}%</span>` : ''}
            <button class="wish-btn ${state.wishlist.has(p.id) ? 'active' : ''}" onclick="toggleWishlist('${p.id}', event)">
              <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
            </button>
          </div>
          <div class="product-info">
            <div class="product-cat">${p.category}</div>
            <div class="product-name">${p.name}</div>
            <div class="product-rating">
              <span style="color:var(--gold);">★</span> ${p.rating}
              <span style="color:var(--on-surface-variant); font-size:10px;">(${p.reviews})</span>
            </div>
            <div class="price-row">
              <span class="curr-price">$${p.price.toFixed(2)}</span>
              ${p.originalPrice > p.price ? `<span class="orig-price">$${p.originalPrice.toFixed(2)}</span>` : ''}
            </div>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

function renderCategories(container) {
  container.innerHTML = `
    <div style="padding: 16px 16px 8px;">
      <div style="font-family:'Cinzel',serif; font-size:20px; font-weight:700;">COLLECTIONS</div>
      <div style="font-size:12px; color:var(--on-surface-variant);">Browse luxury departments</div>
    </div>
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px; padding:12px 16px;">
      ${categories.map(c => `
        <div onclick="navigateTo('listing', '${c.name}')" style="position:relative; height:140px; border-radius:14px; overflow:hidden; cursor:pointer;">
          <img src="${c.img}" style="width:100%; height:100%; object-fit:cover;">
          <div style="position:absolute; inset:0; background:linear-gradient(to top, rgba(0,0,0,0.8), transparent); padding:10px; display:flex; flex-direction:column; justify-content:flex-end; color:#fff;">
            <span style="font-size:10px; background:rgba(255,255,255,0.2); padding:2px 6px; border-radius:4px; align-self:flex-start;">${c.count} items</span>
            <span style="font-family:'Cinzel',serif; font-size:14px; font-weight:700; margin-top:4px;">${c.name}</span>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

function renderListing(container) {
  const cat = state.activeArg || 'All';
  const list = products.filter(p => cat === 'All' || p.category === cat);
  container.innerHTML = `
    <div style="padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
      <span style="font-weight:700; font-size:15px;">${list.length} Items</span>
      <span style="font-size:12px; color:var(--on-surface-variant);">Filtered by ${cat}</span>
    </div>
    <div class="products-grid">
      ${list.map(p => `
        <div class="product-card" onclick="navigateTo('details', '${p.id}')">
          <div class="product-img-wrap">
            <img src="${p.img}" alt="${p.name}">
            ${p.discount ? `<span class="discount-badge">-${p.discount}%</span>` : ''}
            <button class="wish-btn ${state.wishlist.has(p.id) ? 'active' : ''}" onclick="toggleWishlist('${p.id}', event)">
              <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
            </button>
          </div>
          <div class="product-info">
            <div class="product-cat">${p.category}</div>
            <div class="product-name">${p.name}</div>
            <div class="price-row">
              <span class="curr-price">$${p.price.toFixed(2)}</span>
              ${p.originalPrice > p.price ? `<span class="orig-price">$${p.originalPrice.toFixed(2)}</span>` : ''}
            </div>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

function renderDetails(container) {
  const prod = products.find(p => p.id === state.activeArg) || products[0];
  container.innerHTML = `
    <div style="position:relative; width:100%; height:320px; background:var(--surface-variant);">
      <img src="${prod.img}" style="width:100%; height:100%; object-fit:cover;">
      <button class="wish-btn ${state.wishlist.has(prod.id) ? 'active' : ''}" style="top:16px; right:16px; width:40px; height:40px;" onclick="toggleWishlist('${prod.id}', event)">
        <svg width="18" height="18" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
      </button>
    </div>
    <div style="padding:16px;">
      <div style="font-size:11px; color:var(--on-surface-variant); letter-spacing:1px; text-transform:uppercase;">${prod.category}</div>
      <div style="font-family:'Cinzel',serif; font-size:20px; font-weight:700; margin:6px 0;">${prod.name}</div>
      <div style="display:flex; align-items:center; gap:8px; margin-bottom:12px;">
        <span style="color:var(--gold);">★ ${prod.rating}</span>
        <span style="font-size:12px; color:var(--on-surface-variant);">• ${prod.reviews} customer reviews</span>
      </div>
      <div style="font-size:22px; font-weight:800; margin-bottom:16px;">
        $${prod.price.toFixed(2)}
        <span style="font-size:14px; text-decoration:line-through; color:var(--on-surface-variant); margin-left:8px;">$${prod.originalPrice.toFixed(2)}</span>
      </div>

      <div style="font-size:12px; font-weight:700; margin-bottom:8px;">AVAILABLE SIZES</div>
      <div style="display:flex; gap:8px; margin-bottom:20px;">
        ${prod.sizes.map(s => `<button style="padding:8px 16px; border-radius:8px; border:1px solid var(--outline); background:var(--surface); color:var(--on-surface); font-weight:600; cursor:pointer;">${s}</button>`).join('')}
      </div>

      <div style="font-size:12px; font-weight:700; margin-bottom:6px;">DESCRIPTION</div>
      <div style="font-size:13px; line-height:1.5; color:var(--on-surface-variant); margin-bottom:24px;">
        ${prod.desc}
      </div>

      <button onclick="addToBag('${prod.id}')" style="width:100%; height:50px; border-radius:12px; background:var(--primary); color:var(--on-primary); border:none; font-size:14px; font-weight:700; cursor:pointer;">
        ADD TO BAG • $${prod.price.toFixed(2)}
      </button>
    </div>
  `;
}

function addToBag(prodId) {
  const existing = state.cart.find(i => i.productId === prodId);
  if (existing) {
    existing.qty += 1;
  } else {
    state.cart.push({ id: 'cart_' + Date.now(), productId: prodId, size: 'M', color: 'Black', qty: 1 });
  }
  updateBadges();
  navigateTo('cart');
}

function renderWishlist(container) {
  const wishProds = products.filter(p => state.wishlist.has(p.id));
  if (!wishProds.length) {
    container.innerHTML = `
      <div style="text-align:center; padding:60px 20px;">
        <div style="font-size:40px; margin-bottom:12px;">♡</div>
        <div style="font-family:'Cinzel',serif; font-size:18px; font-weight:700;">YOUR WISHLIST IS EMPTY</div>
        <div style="font-size:12px; color:var(--on-surface-variant); margin:8px 0 20px;">Save pieces you covet to view anytime.</div>
        <button onclick="navigateTo('home')" class="btn-action primary" style="margin:auto;">Explore Collection</button>
      </div>
    `;
    return;
  }
  container.innerHTML = `
    <div style="padding:16px;">
      <div style="font-weight:700; font-size:16px; margin-bottom:12px;">Saved Items (${wishProds.length})</div>
      <div class="products-grid">
        ${wishProds.map(p => `
          <div class="product-card" onclick="navigateTo('details', '${p.id}')">
            <div class="product-img-wrap">
              <img src="${p.img}" alt="${p.name}">
              <button class="wish-btn active" onclick="toggleWishlist('${p.id}', event)">
                <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
              </button>
            </div>
            <div class="product-info">
              <div class="product-name">${p.name}</div>
              <div class="price-row"><span class="curr-price">$${p.price.toFixed(2)}</span></div>
            </div>
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

function renderCart(container) {
  if (!state.cart.length) {
    container.innerHTML = `
      <div style="text-align:center; padding:60px 20px;">
        <div style="font-size:40px; margin-bottom:12px;">👜</div>
        <div style="font-family:'Cinzel',serif; font-size:18px; font-weight:700;">YOUR BAG IS EMPTY</div>
        <div style="font-size:12px; color:var(--on-surface-variant); margin:8px 0 20px;">Explore our bespoke seasonal items.</div>
        <button onclick="navigateTo('home')" class="btn-action primary" style="margin:auto;">Start Shopping</button>
      </div>
    `;
    return;
  }

  let subtotal = 0;
  const itemsHtml = state.cart.map(item => {
    const p = products.find(prod => prod.id === item.productId) || products[0];
    const lineTotal = p.price * item.qty;
    subtotal += lineTotal;
    return `
      <div style="display:flex; gap:12px; background:var(--surface); border:1px solid var(--outline); border-radius:12px; padding:10px; margin-bottom:10px; align-items:center;">
        <img src="${p.img}" style="width:65px; height:65px; object-fit:cover; border-radius:8px;">
        <div style="flex:1;">
          <div style="font-size:13px; font-weight:600;">${p.name}</div>
          <div style="font-size:11px; color:var(--on-surface-variant);">Size: ${item.size} • $${p.price.toFixed(2)}</div>
          <div style="font-size:14px; font-weight:800; margin-top:4px;">$${lineTotal.toFixed(2)}</div>
        </div>
        <div style="display:flex; align-items:center; gap:6px; border:1px solid var(--outline); border-radius:8px; padding:2px 8px;">
          <button onclick="updateCartQty('${item.id}', -1)" style="background:none; border:none; cursor:pointer; font-weight:bold; color:var(--on-surface);">-</button>
          <span style="font-size:12px; font-weight:700;">${item.qty}</span>
          <button onclick="updateCartQty('${item.id}', 1)" style="background:none; border:none; cursor:pointer; font-weight:bold; color:var(--on-surface);">+</button>
        </div>
      </div>
    `;
  }).join('');

  const shipping = subtotal > 200 ? 0 : 15.0;
  const total = subtotal + shipping;

  container.innerHTML = `
    <div style="padding:16px;">
      <div style="font-weight:700; font-size:16px; margin-bottom:12px;">Cart Items</div>
      ${itemsHtml}

      <div style="background:var(--surface); border:1px solid var(--outline); border-radius:12px; padding:14px; margin-top:16px;">
        <div style="display:flex; justify-content:space-between; font-size:13px; margin-bottom:8px;">
          <span style="color:var(--on-surface-variant);">Subtotal</span>
          <span style="font-weight:600;">$${subtotal.toFixed(2)}</span>
        </div>
        <div style="display:flex; justify-content:space-between; font-size:13px; margin-bottom:8px;">
          <span style="color:var(--on-surface-variant);">Shipping</span>
          <span style="font-weight:600;">${shipping === 0 ? 'FREE' : '$' + shipping.toFixed(2)}</span>
        </div>
        <div style="border-top:1px solid var(--outline); padding-top:8px; display:flex; justify-content:space-between; font-size:16px; font-weight:800;">
          <span>Total</span>
          <span>$${total.toFixed(2)}</span>
        </div>
      </div>

      <button onclick="navigateTo('checkout')" style="width:100%; height:50px; border-radius:12px; background:var(--primary); color:var(--on-primary); border:none; font-size:14px; font-weight:700; cursor:pointer; margin-top:16px;">
        PROCEED TO CHECKOUT • $${total.toFixed(2)}
      </button>
    </div>
  `;
}

function updateCartQty(cartId, delta) {
  const idx = state.cart.findIndex(i => i.id === cartId);
  if (idx >= 0) {
    state.cart[idx].qty += delta;
    if (state.cart[idx].qty <= 0) {
      state.cart.splice(idx, 1);
    }
  }
  renderScreen();
}

function renderCheckout(container) {
  container.innerHTML = `
    <div style="padding:16px;">
      <div style="background:var(--surface); border:1px solid var(--outline); border-radius:12px; padding:14px; margin-bottom:14px;">
        <div style="font-size:11px; font-weight:800; letter-spacing:1px; color:var(--secondary); margin-bottom:4px;">SHIPPING ADDRESS</div>
        <div style="font-weight:700; font-size:14px;">Sophia Montgomery</div>
        <div style="font-size:12px; color:var(--on-surface-variant);">742 Evergreen Terrace, Apt 4B</div>
        <div style="font-size:12px; color:var(--on-surface-variant);">San Francisco, CA 94107</div>
      </div>

      <div style="background:var(--surface); border:1px solid var(--outline); border-radius:12px; padding:14px; margin-bottom:14px;">
        <div style="font-size:11px; font-weight:800; letter-spacing:1px; color:var(--secondary); margin-bottom:4px;">PAYMENT METHOD</div>
        <div style="font-weight:600; font-size:13px;">Mastercard ending in •••• 4242</div>
        <div style="font-size:11px; color:var(--on-surface-variant);">Instant confirmation with SSL 256-bit encryption</div>
      </div>

      <button onclick="placeOrder()" style="width:100%; height:50px; border-radius:12px; background:var(--primary); color:var(--on-primary); border:none; font-size:14px; font-weight:700; cursor:pointer; margin-top:10px;">
        CONFIRM & PLACE ORDER
      </button>
    </div>
  `;
}

function placeOrder() {
  const newOrd = {
    id: 'ord_' + Date.now(),
    num: 'AUR-' + Math.floor(10000 + Math.random() * 90000),
    date: 'Today',
    total: state.cart.reduce((s, i) => s + i.qty * 200, 0),
    status: 'Processing',
    items: state.cart.map(i => i.productId)
  };
  state.orders.unshift(newOrd);
  state.cart = [];
  updateBadges();
  navigateTo('orders');
}

function renderOrders(container) {
  container.innerHTML = `
    <div style="padding:16px;">
      <div style="font-weight:700; font-size:16px; margin-bottom:12px;">Order History</div>
      ${state.orders.map(o => `
        <div style="background:var(--surface); border:1px solid var(--outline); border-radius:12px; padding:14px; margin-bottom:12px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
            <span style="font-weight:700; font-size:14px;">${o.num}</span>
            <span style="font-size:11px; font-weight:700; padding:2px 8px; border-radius:4px; background:${o.status==='Delivered'?'rgba(46,105,48,0.15)':'rgba(197,168,128,0.2)'}; color:${o.status==='Delivered'?'var(--green)':'var(--secondary)'};">${o.status}</span>
          </div>
          <div style="font-size:11px; color:var(--on-surface-variant); margin-bottom:8px;">Placed on ${o.date}</div>
          <div style="display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--outline); padding-top:8px;">
            <span style="font-weight:800; font-size:15px;">$${o.total.toFixed(2)}</span>
            <span style="font-size:12px; color:var(--secondary); font-weight:600;">Track Package →</span>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

function renderProfile(container) {
  container.innerHTML = `
    <div style="padding:16px;">
      <div style="display:flex; align-items:center; gap:14px; background:var(--surface); border:1px solid var(--outline); border-radius:14px; padding:16px; margin-bottom:16px;">
        <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80" style="width:60px; height:60px; border-radius:50%; object-fit:cover;">
        <div>
          <span style="font-size:10px; font-weight:700; background:rgba(197,168,128,0.2); color:var(--secondary); padding:2px 6px; border-radius:4px;">VIP GOLD TIER</span>
          <div style="font-family:'Cinzel',serif; font-size:16px; font-weight:700; margin:3px 0 2px;">Sophia Montgomery</div>
          <div style="font-size:12px; color:var(--on-surface-variant);">sophia.montgomery@example.com</div>
        </div>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px; margin-bottom:16px;">
        <div onclick="navigateTo('orders')" style="background:var(--surface); border:1px solid var(--outline); border-radius:12px; padding:12px; cursor:pointer;">
          <div style="font-size:16px; font-weight:700;">${state.orders.length} Orders</div>
          <div style="font-size:11px; color:var(--on-surface-variant);">View History →</div>
        </div>
        <div onclick="navigateTo('wishlist')" style="background:var(--surface); border:1px solid var(--outline); border-radius:12px; padding:12px; cursor:pointer;">
          <div style="font-size:16px; font-weight:700;">${state.wishlist.size} Saved</div>
          <div style="font-size:11px; color:var(--on-surface-variant);">My Wishlist →</div>
        </div>
      </div>

      <div style="background:var(--surface); border:1px solid var(--outline); border-radius:12px; overflow:hidden;">
        <div onclick="toggleTheme()" style="padding:14px; display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--outline); cursor:pointer;">
          <span style="font-size:13px; font-weight:600;">Theme: ${state.theme === 'light' ? 'Light Mode' : 'Dark Mode'}</span>
          <span style="font-size:12px; color:var(--secondary);">Switch ⟳</span>
        </div>
        <div onclick="openCodeModal()" style="padding:14px; display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--outline); cursor:pointer;">
          <span style="font-size:13px; font-weight:600;">Kotlin Jetpack Compose Sources</span>
          <span style="font-size:12px; color:var(--secondary);">Explore →</span>
        </div>
        <div style="padding:14px; display:flex; justify-content:space-between; align-items:center; color:var(--tertiary); cursor:pointer;" onclick="navigateTo('home')">
          <span style="font-size:13px; font-weight:600;">Sign Out</span>
          <span>↪</span>
        </div>
      </div>
    </div>
  `;
}

let kotlinFiles = [];
async function openCodeModal() {
  document.getElementById('codeModal').style.display = 'flex';
  if (!kotlinFiles.length) {
    try {
      const res = await fetch('/api/kotlin-files');
      kotlinFiles = await res.json();
      renderFilesList();
    } catch(e) {
      document.getElementById('codeContentDisplay').innerText = 'Could not load files: ' + e;
    }
  }
}
function closeCodeModal() {
  document.getElementById('codeModal').style.display = 'none';
}
function renderFilesList() {
  const list = document.getElementById('codeFilesList');
  list.innerHTML = kotlinFiles.map((f, i) => `
    <button class="code-file-btn ${i===0?'active':''}" onclick="selectFile(${i}, this)">
      ${f.name}
    </button>
  `).join('');
  if (kotlinFiles[0]) {
    document.getElementById('codeContentDisplay').innerText = kotlinFiles[0].content;
  }
}
function selectFile(index, btn) {
  document.querySelectorAll('.code-file-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  document.getElementById('codeContentDisplay').innerText = kotlinFiles[index].content;
}

// Start app
document.addEventListener('DOMContentLoaded', () => {
  renderScreen();
});
if (document.readyState !== 'loading') {
  renderScreen();
}
