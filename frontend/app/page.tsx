'use client';
import { useEffect } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { getToken } from '@/lib/auth';
import { useRouter } from 'next/navigation';

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = getToken();
    if (token) {
      router.push('/trips');
    }
  }, [router]);

  return (
    <div className="section py-6 px-6 md:px-12 border">
      <div className="flex flex-col-reverse lg:flex-row items-center lg:gap-10 gap-4">
        <div className="flex-1">
          <h1 className="text-heading1-bold">Group travel, made simple.</h1>
          <p className="mt-4">
            Plan, vote on activities, and keep everything organized with
            FareShare. From destinations to shared expenses — make every trip
            effortless and unforgettable, together.
          </p>
          <button className="primary-btn text-heading4-medium px-16 py-3 mt-4">
            <Link href="/sign-up">Get Started</Link>
          </button>
        </div>

        <div className="flex-1 w-full max-w-md mx-auto">
          <Image
            src="/assets/trip-placeholder.png"
            alt="Trip placeholder"
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
