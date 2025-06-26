import axios, { AxiosError } from 'axios';
import { IGetProductsResponse, IProduct } from 'models';

const isProduction = process.env.NODE_ENV === 'production';

export type ProductsResult = {
  data: IProduct[] | null;
  error: string | null;
  loading: boolean;
};

const MAX_RETRIES = 3;
const RETRY_DELAY = 500; // ms

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export const getProducts = async (): Promise<ProductsResult> => {
  let retries = 0;
  let lastError: string | null = null;

  while (retries < MAX_RETRIES) {
    try {
      let products: IProduct[] = [];

      if (isProduction) {
        const response = await axios.get<IGetProductsResponse>(
          'https://react-shopping-cart-67954.firebaseio.com/products.json'
        );

        // HTTP status code validation
        if (response.status < 200 || response.status >= 300) {
          throw new Error(`Unexpected response status: ${response.status}`);
        }

        products = response.data.data.products || [];
      } else {
        // Simulate async for local
        const localData: IGetProductsResponse = require('static/json/products.json');
        products = localData.data.products || [];
      }

      return { data: products, error: null, loading: false };
    } catch (error: unknown) {
      let errorMessage = 'An unknown error occurred.';

      if (axios.isAxiosError(error)) {
        if (!error.response) {
          errorMessage = 'Network error: Please check your internet connection.';
        } else if (error.response.status >= 500) {
          errorMessage = 'Server error: Please try again later.';
        } else if (error.response.status >= 400) {
          errorMessage = 'Request error: Unable to fetch products.';
        }
      } else if (error instanceof Error) {
        errorMessage = error.message;
      }

      lastError = errorMessage;
      retries += 1;

      // Retry only for network/server errors
      if (
        axios.isAxiosError(error) &&
        (!error.response || error.response.status >= 500)
      ) {
        await sleep(RETRY_DELAY * retries); // Exponential backoff
        continue;
      } else {
        break;
      }
    }
  }

  return { data: null, error: lastError, loading: false };
};
