const albumsList = document.getElementById('productsList');
const searchBar = document.getElementById('searchInput');
const allProducts = [];
fetch("http://localhost:8000/products/api")
    .then(response => response.json())
    .then(data => {
        for (let d of data) {
            allProducts.push(d);
        }
    });

searchBar.addEventListener('keyup', (e) => {
    const searchingCharacters = searchBar.value.toLowerCase();
    console.log(allProducts);
    let filteredProducts = allProducts.filter(product => {
        return product.name.toLowerCase().includes(searchingCharacters)
            || product.brand.toLowerCase().includes(searchingCharacters);
    });
    console.log(filteredProducts);
    displayProducts(filteredProducts);
})


const displayProducts = (products) => {
    productsList.innerHTML = products
        .map((p) => {
            return `
              <div class="col-md-3" >
                 <div class="card mb-4 box-shadow">
                  <div class="product-image">
                   <img src="${p.image}"  class="card-img-top" alt="Thumbnail [100%x225]" data-holder-rendered="true" style="height: 225px; width: 100%; display: block;">
                   </div>
                 <div class="card-body">
                     <div class="text-center">
                         <h6 id="product-name-text" class="d-inline-block mb-2 text-primary">${p.name}</h6>
                         <p class="card-text border-bottom">Brand: ${p.brand}</p>
                         <p class="card-text border-bottom">Price: ${p.promotionPrice}</p
                     </div>
                     <div class="d-flex justify-content-between align-items-center">
                         <div class="btn-group">
                             <a href="/products/details/${p.name}" style="color:blue"  type="button" class="btn btn-sm btn-outline-secondary">Details</a>
                         </div>
                        </div>
                     </div>
                 </div>
               </div>
             </div>`

        })
        .join('');

}


