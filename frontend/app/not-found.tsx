'use client';
import Link from 'next/link';
import Image from 'next/image';

export default function NotFound() {
  return (
    <div className="section py-6 px-6 md:px-12 border">
      <div className="flex flex-col-reverse lg:flex-row items-center lg:gap-10 gap-4">
        <div className="flex-1 text-center lg:text-left">
          <h1 className="text-heading1-bold">Oops! Page not found.</h1>
          <p className="mt-4 text-base">
            The page you're looking for doesn't exist or has been moved.
            <br />
            Let's get you back on track.
          </p>
          <Link href="/">
            <button className="primary-btn text-heading4-medium px-16 py-3 mt-6">
              Go to Homepage
            </button>
          </Link>
        </div>

        <div className="flex-1 w-full max-w-md mx-auto">
          <Image
            src="/assets/lost-bro.png"
            alt="Page Not Found"
            width={600}
            height={600}
            className="w-full h-auto object-cover"
            priority
          />
        </div>
      </div>
    </div>
  );
}
