import { useCallback } from 'react';

import { useProductsContext } from './ProductsContextProvider';
import { IProduct } from 'models';
import { getProducts } from 'services/products';

const useProducts = () => {
  const {
    isFetching,
    setIsFetching,
    products,
    setProducts,
    filters,
    setFilters,
  } = useProductsContext();

  const fetchProducts = useCallback(() => {
    setIsFetching(true);
    getProducts().then((result) => {
      setIsFetching(false);
      if (result.error) {
        // Optionally handle error, e.g., show a toast or set an error state
        setProducts([]);
      } else {
        setProducts(result.data || []);
      }
    });
  }, [setIsFetching, setProducts]);

  const filterProducts = useCallback((filters: string[]) => {
    setIsFetching(true);

    getProducts().then((result) => {
      setIsFetching(false);
      let filteredProducts: IProduct[] = [];

      console.time('filterProducts'); // Start performance timer

      if (result.error) {
        // Optionally handle error
        filteredProducts = [];
      } else if (result.data && filters && filters.length > 0) {
        const filterSet = new Set(filters);
        filteredProducts = result.data.filter((p: IProduct) =>
          p.availableSizes.some((size: string) => filterSet.has(size))
        );
      } else if (result.data) {
        filteredProducts = result.data;
      }

      console.timeEnd('filterProducts'); // End performance timer

      setFilters(filters);
      setProducts(filteredProducts);
    });
  }, [setFilters, setProducts, setIsFetching]);

  return {
    isFetching,
    fetchProducts,
    products,
    filterProducts,
    filters,
  };
};

export default useProducts;
